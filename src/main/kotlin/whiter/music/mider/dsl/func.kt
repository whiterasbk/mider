package whiter.music.mider.dsl

import whiter.music.mider.MetaEventType
import whiter.music.mider.MidiFile
import whiter.music.mider.convert2MidiMessages
import whiter.music.mider.convert2MusicXml
import whiter.music.mider.xml.MusicXml
import java.io.File
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask
import kotlin.math.log2

fun play(autoClose: Boolean = true, block: MiderDSL.() -> Unit) {
    val pair = playAsync(false, block)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playAsync(autoClose: Boolean = false, block: MiderDSL.() -> Unit): Pair<Long, Sequencer> {
    val sequencer = MidiSystem.getSequencer()
    fromDsl(block).inStream().use {
        sequencer.setSequence(it)
        sequencer.open()
        sequencer.start()
        val delay: Long = sequencer.sequence.microsecondLength / 1000 + 500

        if (autoClose) {
            Timer().schedule(timerTask {
                sequencer.close()
            }, delay)
        }
        return delay to sequencer
    }
}

fun playDslInstance(autoClose: Boolean = true, miderDSL: MiderDSL) {
    val pair = playDslInstanceAsync(false, miderDSL)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playDslInstanceAsync(autoClose: Boolean = false, miderDSL: MiderDSL): Pair<Long, Sequencer> {
    val sequencer = MidiSystem.getSequencer()
    val midiFile = fromDslInstance(miderDSL)
    midiFile.inStream().use {
        sequencer.setSequence(it)
        sequencer.open()
        sequencer.start()
        val delay: Long = sequencer.sequence.microsecondLength / 1000 + 500

        if (autoClose) {
            Timer().schedule(timerTask {
                sequencer.close()
            }, delay)
        }
        return delay to sequencer
    }
}

fun fromDslInstance(dsl: MiderDSL): MidiFile {
    val wholeTicks = 960 * 2 * 2
    val clock: Byte = 18
    val midi = MidiFile()

    fun MidiFile.addTrack(dslObj: MiderDSL) {
        track {
            dslObj.container.mainList
                .convert2MidiMessages(
                    wholeTicks,
                    config = dslObj.convert2MidiEventConfig
                )
                .forEach {
                    append(it)
                }
            end()
        }
    }

    midi.append {
        track {
            tempo(dsl.bpm)

            dsl.timeSignature?.let {
                meta(
                    MetaEventType.META_TIME_SIGNATURE,
                    it.first.toByte(),
                    log2(it.second.toDouble()).toInt().toByte(),
                    clock, 8
                )
            }

            end()
        }

        addTrack(dsl)

        if (dsl.otherTracks.isNotEmpty()) {
            dsl.otherTracks.forEach {
                addTrack(it)
            }
        }
    }

    return midi
}

fun Dsl2MusicXml(dsl: MiderDSL, divisions: Int = 480): MusicXml {
    return dsl.container.mainList.convert2MusicXml(dsl.bpm,
        dsl.timeSignature?.first ?: 4,
        dsl.timeSignature?.second ?: 4,
        divisions,
        dsl.keySignature
    )
}

fun fromDsl(block: MiderDSL.() -> Unit): MidiFile {
    val dsl = MiderDSL()
    dsl.block()
    return fromDslInstance(dsl)
}

fun apply(path: String, block: MiderDSL.() -> Unit) {
    if (path.endsWith(".xml")) {
        val dsl = MiderDSL()
        dsl.block()
        Dsl2MusicXml(dsl).save(File(path))
    } else fromDsl(block).save(path)
}

