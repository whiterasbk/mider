package whiter.music.mider.code

import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.dsl.fromDslInstance
import java.io.InputStream
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
fun miderCodeToMiderDSL(msg: String): MiderDSL {

    val startRegex = Regex(">(g|f|\\d+b)((;[-+b#]?[A-G](min|maj|major|minor)?)|(;\\d)|(;vex|vex&au)|(;midi))*>")
    // val cmdRegex = Regex("${startRegex.pattern}[\\S\\s]+")

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

                    }
                }

                val sequence = macro(content)

                val isStave =
                    Regex("[c-gaA-G]").find(sequence) != null || Regex("(\\s*b\\s*)+").matches(sequence)

                val rendered = toInMusicScoreList(
                    sequence,
                    isStave = isStave,
                    pitch = defaultPitch, useMacro = false
                )

                ifUseMode(mode) {
                    val stander = toMiderStanderNoteString(rendered)
                    if (stander.isNotBlank()) !stander
                }
            }
        }
    }

    val dsl = MiderDSL()
    dsl.dslBlock()
    return dsl
}

private fun MiderDSL.ifUseMode(mode: String, block: MiderDSL.()-> Unit) {
    if (mode.isNotBlank()) {
        useMode(mode) {
            block()
        }
    } else block()
}

