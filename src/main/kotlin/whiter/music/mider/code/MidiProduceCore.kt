package whiter.music.mider.code

import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.dsl.fromDslInstance
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask


fun miderCodeToMiderDSL(msg: String): MiderDSL {
    var arrowCount = 0
    var availCount = 0
    var defaultBmp = 80
    var defaultPitch = 4
    var mode = ""

    msg.forEach {
        if (arrowCount >= 2) return@forEach
        if (it == '>') arrowCount ++
        availCount ++
    }

    val noteList = msg.substring(availCount, msg.length)//.replace(Regex("\\s*"), "")
    val configPart = msg.substring(0, availCount).replace(">", "").split(";")

    configPart.forEach {
        if (it.matches(Regex("\\d+b"))) {
            defaultBmp = it.replace("b", "").toInt()
        } else if (it.matches(Regex("[-+b#]?[A-G](min|maj|major|minor)?"))) {
            mode = it
        } else if (it.matches(Regex("\\d"))) {
            defaultPitch = it.toInt()
        }
    }

    val mdsl = MiderDSL()

    with(mdsl) {
        bpm = defaultBmp

        if (noteList.matches(Regex("[0-9.\\s-+*/|↑↓i!#b&]+"))) {
            if (noteList.trim().matches(Regex("b+"))) {
                ifUseMode(mode) { !toMiderNoteList(noteList, defaultPitch) }
            } else {
                if (defaultPitch != 4) pitch = defaultPitch.toByte()
                ifUseMode(mode) { parseInt(noteList.replace(Regex("( {2}| \\| )"), "0")) }
            }
        } else {
            ifUseMode(mode) { !toMiderNoteList(noteList, defaultPitch) }
        }
    }

    return mdsl
}

private fun MiderDSL.ifUseMode(mode: String, block: MiderDSL.()-> Unit) {
    if (mode.isNotBlank()) {
        useMode(mode) {
            block()
        }
    } else block()
}

