package org.mider.dsl

import org.mider.inStream
import org.mider.save
import org.mider.xml.save
import java.io.File
import java.io.InputStream
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask

fun play(autoClose: Boolean = true, block: MiderDSL.() -> Unit) {
    val pair = playAsync(false, block)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playAsync(autoClose: Boolean = true, block: MiderDSL.() -> Unit): Pair<Long, Sequencer> {
    val sequencer = MidiSystem.getSequencer()
    return fromDsl(block).inStream().openRecordSequencer(autoClose, sequencer)
}

fun playDslInstance(autoClose: Boolean = true, miderDSL: MiderDSL) {
    val pair = playDslInstanceAsync(false, miderDSL)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playDslInstanceAsync(autoClose: Boolean = true, miderDSL: MiderDSL): Pair<Long, Sequencer> {
    val sequencer = MidiSystem.getSequencer()
    val midiFile = fromDslInstance(miderDSL)
    return midiFile.inStream().openRecordSequencer(autoClose, sequencer)
}

fun apply(path: String, block: MiderDSL.() -> Unit) {
    if (path.endsWith(".xml")) {
        val dsl = MiderDSL()
        dsl.block()
        dsl2MusicXml(dsl).save(File(path))
    } else fromDsl(block).save(path)
}

private fun InputStream.openRecordSequencer(autoClose: Boolean, sequencer: Sequencer) = use {
    sequencer.setSequence(it)
    sequencer.open()
    sequencer.start()
    val delay: Long = sequencer.sequence.microsecondLength / 1000 + 500

    if (autoClose) {
        Timer().schedule(timerTask {
            sequencer.close()
        }, delay)
    }

    delay to sequencer
}