package whiter.music.mider.code

import whiter.music.mider.MidiInstrument
import whiter.music.mider.dsl.MiderDSLv2

val startRegex = Regex(">(g|f|\\d+b)((;[-+b#]?[A-G](min|maj|major|minor)?)|(;\\d)|(;img)|(;pdf)|(;mscz)|(;midi)|(;i=[a-zA-Z-]+)|(;\\d/\\d))*>")

enum class NotationType {
    PNGS, MSCZ, PDF
}

data class ProduceCoreResult(
    var miderDSL: MiderDSLv2 = MiderDSLv2(),
    var isRenderingNotation: Boolean = false,
    var isUploadMidi: Boolean = false,
    var notationType: NotationType? = null,
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
//        val changeOuterProgram = { ins: String -> miderDSL.program = MidiInstrument.valueOf(ins) }
        val changeTimeSignature = { pair: Pair<Int, Int> -> miderDSL.timeSignature = pair }
        // todo 怪, 应该每条轨道都能设置才对

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
                            } else if (it.matches(Regex("i=[a-zA-Z-]+"))) {
                                // 两个都设置下 (
                                program = MidiInstrument.valueOf(it.replace("i=", ""))
//                                changeOuterProgram(it.replace("i=", ""))
//                                // todo fix
//                                if (!config.formatMode.contains("muse-score")) {
//                                    logs.add("set outer program to $program")
//                                }
                                logs.add("set program to $program")
                            }
                        }
                    }
                }

                convert2MidiEventConfig = config.convertMidiEventConfiguration

                val sequence = macro(content, config.macroConfiguration)

                val isStave =
                    Regex("[c-gaA-G]").find(sequence) != null || Regex("(\\s*b\\s*)+").matches(sequence)

//                val rendered = toInMusicScoreList(
//                    sequence.let {
//                        if (isStave && config._isBlankReplaceWith0) it else
//                            it.trim().replace(Regex("( {2}| \\| )"), "0")
//                    },
//                    isStave = isStave,
//                    pitch = pitch, useMacro = false
//                )

                val execBlock = {
                    (sequence.let {
                        if (isStave && config._isBlankReplaceWith0) it else
                            it.trim().replace(Regex("( {2}| \\| )"), "0")
                    })(isStave, useMacro = false)
                }

                if (mode.isNotBlank()) {
                    mode {
                        execBlock()
                    }
                } else execBlock()

//                ifUseMode(mode) {
//
//                    val stander = toMiderStanderNoteString(rendered)
//                    if (stander.isNotBlank()) !stander
//                }

                logs.add("track: ${index + 1}") //; debug()
            }

        }
    }

    val result = ProduceCoreResult()
    result.build()
    return result
}

@Deprecated(message = "will be delete")
private fun MiderDSLv2.ifUseMode(mode: String, block: MiderDSLv2.()-> Unit) {
    if (mode.isNotBlank()) {
        mode {
            block()
        }
    } else block()
}

