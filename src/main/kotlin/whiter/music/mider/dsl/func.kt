package whiter.music.mider.dsl

import whiter.music.mider.MetaEventType
import whiter.music.mider.MidiFile
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask
import kotlin.math.abs
import kotlin.math.log2

fun play(autoClose: Boolean = true, block: MiderDSL.() -> Any) {
    val pair = playAsync(false, block)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playAsync(autoClose: Boolean = false, block: MiderDSL.() -> Any): Pair<Long, Sequencer> {
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

fun fromDsl(block: MiderDSL.() -> Any): MidiFile {
    val mdsl = MiderDSL()
    val minimsTicks = 960
    val clock: Byte = 18
    with(mdsl, block)
    val midi = MidiFile()
    midi.append {
        track {
            tempo(mdsl.bpm)

            mdsl.keySignature?.let {
                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
            }

            mdsl.timeSignature?.let {
                meta(MetaEventType.META_TIME_SIGNATURE, it.first.toByte(), log2(it.second.toDouble()).toInt().toByte(), clock, 8)
            }

            end()
        }

        track {
            changeProgram(mdsl.program.id.toByte())
            parseSound(minimsTicks, mdsl.adjustedList())
            if (mdsl.otherTracks.isNotEmpty()) {
                mdsl.otherTracks.forEach {
                    track {
                        changeProgram(it.program.id.toByte())
                        parseSound(minimsTicks, it.adjustedList())
                        end()
                    }
                }
            }
            end()
        }
    }

    return midi
}

fun apply(path: String, block: MiderDSL.() -> Any) {
    val mdsl = MiderDSL()
    val minimsTicks = 960
    val clock: Byte = 18
    with(mdsl, block)
    val midi = MidiFile()
    midi.append {
        track {
            tempo(mdsl.bpm)

            mdsl.keySignature?.let {
                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
            }

            mdsl.timeSignature?.let {
                meta(MetaEventType.META_TIME_SIGNATURE, it.first.toByte(), log2(it.second.toDouble()).toInt().toByte(), clock, 8)
            }

            end()
        }

        track {
            changeProgram(mdsl.program.id.toByte())

            parseSound(minimsTicks, mdsl.adjustedList())

            end()
        }

        if (mdsl.otherTracks.isNotEmpty()) {
            mdsl.otherTracks.forEach {
                track {
                    changeProgram(it.program.id.toByte())
                    parseSound(minimsTicks, it.adjustedList())
                    end()
                }
            }
        }
    }
    midi.save(path)
}

