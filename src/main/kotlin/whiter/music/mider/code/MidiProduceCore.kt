package whiter.music.mider.code

import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.dsl.fromDsl
import whiter.music.mider.dsl.fromDslInstance
import java.io.InputStream
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask


fun miderCodeToMiderDSL(msg: String, Config: MiderCodeParseCondition, macroConfig: MacroConfiguration): MiderDSL.() -> Unit {
    val startRegex = Regex(">(g|f|\\d+b)((;[-+b#]?[A-G](min|maj|major|minor)?)|(;\\d)|(;vex|vex&au)|(;midi))*>")

    val noteLists = msg.split(startRegex).toMutableList()
    noteLists.removeFirst()
    val configParts = startRegex.findAll(msg).map { it.value.replace(">", "") }.toList()

    val dslBlock: MiderDSL.() -> Unit = {

        val changeBpm = { tempo: Int -> bpm = tempo }

        noteLists.forEachIndexed { index, content ->

            track {
                var mode = ""
                var defaultPitch = 4

                defaultNoteDuration = 1

                configParts[index].split(";").forEach {
                    if (it == "f") {
                        defaultPitch = 3
                    } else if (it.matches(Regex("\\d+b"))) {
                        changeBpm(it.replace("b", "").toInt())
                    } else if (it.matches(Regex("[-+b#]?[A-G](min|maj|major|minor)?"))) {
                        mode = it
                    } else if (it.matches(Regex("\\d"))) {
                        defaultPitch = it.toInt()
                    } else if (it.matches(Regex("vex|wex&au"))) {
                        // todo 渲染乐谱
                    }
                }

                val sequence = macro(content, macroConfig)

                val isStave =
                    Regex("[c-gaA-G]").find(sequence) != null || Regex("(\\s*b\\s*)+").matches(sequence)

                val rendered = toInMusicScoreList(
                    sequence.let {
                        if (isStave && Config.isBlankReplaceWith0) it else
                            it.trim().replace(Regex("( {2}| \\| )"), "0")
                    },
                    isStave = isStave,
                    pitch = defaultPitch, useMacro = false
                )

                ifUseMode(mode) {
                    val stander = toMiderStanderNoteString(rendered)
                    if (stander.isNotBlank()) !stander
                }

                // 渲染 乐谱

            }

        }
    }

    return dslBlock
}

private fun MiderDSL.ifUseMode(mode: String, block: MiderDSL.()-> Unit) {
    if (mode.isNotBlank()) {
        useMode(mode) {
            block()
        }
    } else block()
}

fun deriveInterval(index: Int, scale: Array<Int> = arrayOf(2, 2, 1, 2, 2, 2, 1)): Int {
    var sum = 0
    for (i in 0 until index) {
        sum += scale[i]
    }
    return sum
}

fun charCount(str: CharSequence, char: Char): Int {
    return str.filter { it == char }.count()
}

class MiderCodeParseCondition {
    //@ValueDescription("是否启用空格替换")
    var isBlankReplaceWith0: Boolean = true

}