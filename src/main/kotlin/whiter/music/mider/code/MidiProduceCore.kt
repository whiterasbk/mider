package whiter.music.mider.code

import whiter.music.mider.MidiInstrument
import whiter.music.mider.dsl.MiderDSL

val startRegex = Regex(
    ">(g|f|\\d+b)((;[-+b#]?[A-G](min|maj|major|minor)?)|(;\\d)|" +
           "(;img)|(;pdf)|(;mscz)|(;sing(:[a-zA-Z-]{2,4})?(:[fm]?\\d+)?)|" +
           "(;midi)|(;\\d{1,3}%)|(;/\\d+)|(;\\d+dB)|(;[↑↓]+)|(;\\d+(\\.\\d+)?x)|" +
           "(;i=([a-zA-Z-]+|\\d+))|(;\\d/\\d))*>"
)

enum class NotationType {
    PNGS, MSCZ, PDF
}

data class ProduceCoreResult(
    var miderDSL: MiderDSL = MiderDSL(),
    var isRenderingNotation: Boolean = false,
    var isUploadMidi: Boolean = false,
    var notationType: NotationType? = null,
    var isSing: Boolean = false,
    var singSong: Pair<String, String>? = null ,  //  String: 国家, Int: 歌手代号
    val logs: MutableList<String> = ArrayList()
)

fun produceCore(msg: String, config: MiderCodeParserConfiguration = MiderCodeParserConfiguration()): ProduceCoreResult {

    val noteLists = msg.split(startRegex).toMutableList()
    noteLists.removeFirst()
    val configParts = startRegex.findAll(msg).map { it.value.replace(">", "") }.toList()

    /*
     * 在这个块中，ProduceCoreResult 的各个成员被修改，所以是 ProduceCoreResult 的拓展函数
     */
    val build: ProduceCoreResult.() -> Unit = {
        val changeBpm = { tempo: Int -> miderDSL.bpm = tempo }
        val changeTimeSignature = { pair: Pair<Int, Int> -> miderDSL.timeSignature = pair }

        noteLists.forEachIndexed { index, content ->

            miderDSL.track {
                var mode = ""

                configParts[index].split(";").forEach {
                    when (it) {

                        "g" -> pitch = 4 // 这样应该能提升性能吧(

                        "f" -> pitch = 3

                        "midi" -> isUploadMidi = true

                        "img" -> {
                            isRenderingNotation = true
                            notationType = NotationType.PNGS
                        }

                        "pdf" -> {
                            isRenderingNotation = true
                            notationType = NotationType.PDF
                        }

                        "mscz" -> {
                            isRenderingNotation = true
                            notationType = NotationType.MSCZ
                        }

                        else -> {
                            if (it.matches(Regex("\\d+b"))) {
                                bpm = it.replace("b", "").toInt()
                                changeBpm(it.replace("b", "").toInt())
                            } else if (it.matches(Regex("[-+b#]?[A-G](min|maj|major|minor)?"))) {
                                mode = it
                            } else if (it.matches(Regex("\\d"))) {
                                pitch = it.toInt()
                            } else if (it.matches(Regex("\\d/\\d"))) {
                                val ts = it.split("/")
                                changeTimeSignature(ts[0].toInt() to ts[1].toInt())
                            } else if (it.matches(Regex("i=([a-zA-Z-]+|\\d+)"))) {
                                program = MidiInstrument.valueOf(it.replace("i=", ""))
                                logs.add("set program to $program")
                            } else if (it.matches(Regex("sing(:[a-zA-Z-]{2,4})?(:[fm]?\\d+)?"))) {
                                isSing = true
                                val ss = it.split(":")
                                when(ss.size) {
                                    1 -> singSong = "cn" to "f1"
                                    2 -> singSong = "+id" to ss[1]
                                    3 -> singSong = ss[1] to ss[2]
                                }
                            } else if (it.matches(Regex("\\d{1,3}%"))) {
                                val v = it.replace("%", "").toInt()
                                if (v in 0..100) volume = v.toFloat() / 100
                            } else if (it.matches(Regex("/\\d+"))) {
                                val p = it.replace("/", "").toInt()
                                duration = 1.0 / p
                            } else if (it.matches(Regex("\\d+dB"))) {
                                val v = it.replace("dB", "").toInt()
                                if (v in 0..127) velocity = v
                            } else if (it.matches(Regex("\\d+(\\.\\d+)?x"))) {
                                duration /= it.replace("x", "").toDouble()
                            } else if (it.matches(Regex("[↑↓]+"))) {
                                // pitch higher or lower
                            }
                        }
                    }
                }

                convert2MidiEventConfig = config.convertMidiEventConfiguration

                val sequence = macro(content, config.macroConfiguration)

                val isStave =
                    Regex("[c-gaA-G]").find(sequence) != null || Regex("(\\s*b\\s*)+").matches(sequence)

                val execBlock = {
                    sequence.let {
                        if (!isStave && config.isBlankReplaceWith0) {
                            // 如果不是五线谱 且 要替换
                            it.trim().replace(Regex("( {2}| \\| )"), "0")
                        } else it
                    } (isStave, useMacro = false)
                }

                if (mode.isNotBlank()) {
                    mode {
                        execBlock()
                    }
                } else execBlock()

                logs.add("track: ${index + 1}")
                container.mainList.forEach {
                    logs.add("adding: $it")
                }
            }

        }
    }

    val result = ProduceCoreResult()
    result.build()

    return result
}

@Deprecated(message = "will be delete")
private fun MiderDSL.ifUseMode(mode: String, block: MiderDSL.()-> Unit) {
    if (mode.isNotBlank()) {
        mode {
            block()
        }
    } else block()
}

