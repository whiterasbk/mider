package whiter.music.mider.dsl

import whiter.music.mider.MetaEventType
import whiter.music.mider.MidiFile
import whiter.music.mider.convert2MidiMessages
import whiter.music.mider.convert2MusicXml
import whiter.music.mider.xml.MusicXml
import java.util.*
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequencer
import kotlin.concurrent.timerTask
import kotlin.math.abs
import kotlin.math.log2

@Deprecated(message = "contents will be replaced")
fun play(autoClose: Boolean = true, block: MiderDSL.() -> Any) {
    val pair = playAsync(false, block)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playv2(autoClose: Boolean = true, block: MiderDSLv2.() -> Unit) {
    val pair = playAsyncv2(false, block)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

@Deprecated(message = "contents will be replaced")
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

fun playAsyncv2(autoClose: Boolean = false, block: MiderDSLv2.() -> Unit): Pair<Long, Sequencer> {
    val sequencer = MidiSystem.getSequencer()
    fromDslv2(block).inStream().use {
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

@Deprecated(message = "contents will be replaced")
fun playDslInstance(autoClose: Boolean = true, miderDSL: MiderDSL) {
    val pair = playDslInstanceAsync(false, miderDSL)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

fun playDslInstancev2(autoClose: Boolean = true, miderDSL: MiderDSLv2) {
    val pair = playDslInstanceAsyncv2(false, miderDSL)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

@Deprecated(message = "contents will be replaced")
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

fun playDslInstanceAsyncv2(autoClose: Boolean = false, miderDSL: MiderDSLv2): Pair<Long, Sequencer> {
    val sequencer = MidiSystem.getSequencer()
    val midiFile = fromDslInstancev2(miderDSL)
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

@Deprecated(message = "contents will be replaced")
fun fromDslInstance(mdsl: MiderDSL): MidiFile {
    val minimsTicks = 960
    val clock: Byte = 18
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

fun fromDslInstancev2(dsl: MiderDSLv2): MidiFile {
    val wholeTicks = 960 * 2 * 2
    val clock: Byte = 18
    val midi = MidiFile()

    fun MidiFile.addTrack(dslObj: MiderDSLv2) {
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

//            dsl.keySignature?.let {
//                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
//            }

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

fun Dsl2MusicXml(dsl: MiderDSLv2, divisions: Int = 480): MusicXml {
    return dsl.container.mainList.convert2MusicXml(dsl.bpm,
        dsl.timeSignature?.first ?: 4,
        dsl.timeSignature?.second ?: 4,
        divisions,
        dsl.keySignature
    )
}

@Deprecated(message = "contents will be replaced")
fun fromDsl(block: MiderDSL.() -> Any): MidiFile {
    val mdsl = MiderDSL()
    with(mdsl, block)
    return fromDslInstance(mdsl)
}
fun fromDslv2(block: MiderDSLv2.() -> Unit): MidiFile {
    val dsl = MiderDSLv2()
    dsl.block()
    return fromDslInstancev2(dsl)
}

@Deprecated(message = "contents will be replaced")
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

fun applyv2(path: String, block: MiderDSLv2.() -> Unit) {
    fromDslv2(block).save(path)
}

