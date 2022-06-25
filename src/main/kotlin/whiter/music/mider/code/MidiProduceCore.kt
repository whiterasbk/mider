package whiter.music.mider.code

import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.dsl.fromDslInstance
import java.io.InputStream
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask
import kotlin.contracts.ExperimentalContracts

val startRegex = Regex(">(g|f|\\d+b)((;[-+b#]?[A-G](min|maj|major|minor)?)|(;\\d)|(;img)|(;pdf)|(;mscz)|(;midi)|(;i=[a-zA-Z-]+)|(;\\d/\\d))*>")

enum class NotationType {
    PNGS, MSCZ, PDF
}

data class ProduceCoreResult(
    var miderDSL: MiderDSL = MiderDSL(),
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
     * 在这个块中，ProduceCoreResult的各个成员被修改，所以是ProduceCoreResult的拓展函数
     */
    val build: ProduceCoreResult.() -> Unit = {
        val changeBpm = { tempo: Int -> miderDSL.bpm = tempo }
        val changeOuterProgram = { ins: String -> miderDSL.program = MiderDSL.instrument.valueOf(ins) }
        val changeTimeSignature = { pair: Pair<Int, Int> -> miderDSL.timeSignature = pair }
        // todo 怪, 应该每条轨道都能设置才对

        noteLists.forEachIndexed { index, content ->

            miderDSL.track {
                var mode = ""
                var defaultPitch = 4
                defaultNoteDuration = 1

                configParts[index].split(";").forEach {
                    when (it) {

                        "g" -> defaultPitch = 4 // 这样应该能提升性能吧(

                        "f" -> defaultPitch = 3

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
                                changeBpm(it.replace("b", "").toInt())
                            } else if (it.matches(Regex("[-+b#]?[A-G](min|maj|major|minor)?"))) {
                                mode = it
                            } else if (it.matches(Regex("\\d"))) {
                                defaultPitch = it.toInt()
                            } else if (it.matches(Regex("\\d/\\d"))) {
                                val ts = it.split("/")
                                changeTimeSignature(ts[0].toInt() to ts[1].toInt())
                            } else if (it.matches(Regex("i=[a-zA-Z-]+"))) {
                                // 两个都设置下 (
                                program = MiderDSL.instrument.valueOf(it.replace("i=", ""))
                                // todo fix
                                if (!config.formatMode.contains("muse-score")) {
                                    changeOuterProgram(it.replace("i=", ""))
                                    logs.add("set outer program to $program")
                                }
                                logs.add("set program to $program")
                            }
                        }
                    }
                }

                val sequence = macro(content, config.macroConfiguration)

                val isStave =
                    Regex("[c-gaA-G]").find(sequence) != null || Regex("(\\s*b\\s*)+").matches(sequence)

                val rendered = toInMusicScoreList(
                    sequence.let {
                        if (isStave && config._isBlankReplaceWith0) it else
                            it.trim().replace(Regex("( {2}| \\| )"), "0")
                    },
                    isStave = isStave,
                    pitch = defaultPitch, useMacro = false
                )

                ifUseMode(mode) {
                    val stander = toMiderStanderNoteString(rendered)
                    if (stander.isNotBlank()) !stander
                }

                logs.add("track: ${index + 1}") //; debug()
            }

        }
    }

    val result = ProduceCoreResult()
    result.build()
    return result
}

private fun MiderDSL.ifUseMode(mode: String, block: MiderDSL.()-> Unit) {
    if (mode.isNotBlank()) {
        useMode(mode) {
            block()
        }
    } else block()
}

