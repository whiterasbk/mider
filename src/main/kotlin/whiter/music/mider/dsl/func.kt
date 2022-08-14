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

//@Deprecated(message = "contents will be replaced")
//fun play(autoClose: Boolean = true, block: MiderDSL.() -> Any) {
//    val pair = playAsync(false, block)
//    Thread.sleep(pair.first)
//
//    if (autoClose) {
//        pair.second.close()
//    }
//}

fun play(autoClose: Boolean = true, block: MiderDSL.() -> Unit) {
    val pair = playAsync(false, block)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

//@Deprecated(message = "contents will be replaced")
//fun playAsync(autoClose: Boolean = false, block: MiderDSL.() -> Any): Pair<Long, Sequencer> {
//    val sequencer = MidiSystem.getSequencer()
//    fromDsl(block).inStream().use {
//        sequencer.setSequence(it)
//        sequencer.open()
//        sequencer.start()
//        val delay: Long = sequencer.sequence.microsecondLength / 1000 + 500
//
//        if (autoClose) {
//            Timer().schedule(timerTask {
//                sequencer.close()
//            }, delay)
//        }
//        return delay to sequencer
//    }
//}

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

//@Deprecated(message = "contents will be replaced")
//fun playDslInstance(autoClose: Boolean = true, miderDSL: MiderDSL) {
//    val pair = playDslInstanceAsync(false, miderDSL)
//    Thread.sleep(pair.first)
//
//    if (autoClose) {
//        pair.second.close()
//    }
//}

fun playDslInstance(autoClose: Boolean = true, miderDSL: MiderDSL) {
    val pair = playDslInstanceAsync(false, miderDSL)
    Thread.sleep(pair.first)

    if (autoClose) {
        pair.second.close()
    }
}

//@Deprecated(message = "contents will be replaced")
//fun playDslInstanceAsync(autoClose: Boolean = false, miderDSL: MiderDSL): Pair<Long, Sequencer> {
//    val sequencer = MidiSystem.getSequencer()
//    val midiFile = fromDslInstance(miderDSL)
//    midiFile.inStream().use {
//        sequencer.setSequence(it)
//        sequencer.open()
//        sequencer.start()
//        val delay: Long = sequencer.sequence.microsecondLength / 1000 + 500
//
//        if (autoClose) {
//            Timer().schedule(timerTask {
//                sequencer.close()
//            }, delay)
//        }
//        return delay to sequencer
//    }
//}

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

//@Deprecated(message = "contents will be replaced")
//fun fromDslInstance(mdsl: MiderDSL): MidiFile {
//    val minimsTicks = 960
//    val clock: Byte = 18
//    val midi = MidiFile()
//    midi.append {
//        track {
//            tempo(mdsl.bpm)
//
//            mdsl.keySignature?.let {
//                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
//            }
//
//            mdsl.timeSignature?.let {
//                meta(MetaEventType.META_TIME_SIGNATURE, it.first.toByte(), log2(it.second.toDouble()).toInt().toByte(), clock, 8)
//            }
//
//            end()
//        }
//
//        track {
//            changeProgram(mdsl.program.id.toByte())
//            parseSound(minimsTicks, mdsl.adjustedList())
//            if (mdsl.otherTracks.isNotEmpty()) {
//                mdsl.otherTracks.forEach {
//                    track {
//                        changeProgram(it.program.id.toByte())
//                        parseSound(minimsTicks, it.adjustedList())
//                        end()
//                    }
//                }
//            }
//            end()
//        }
//    }
//
//    return midi
//}

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

//@Deprecated(message = "contents will be replaced")
//fun fromDsl(block: MiderDSL.() -> Any): MidiFile {
//    val mdsl = MiderDSL()
//    with(mdsl, block)
//    return fromDslInstance(mdsl)
//}

fun fromDsl(block: MiderDSL.() -> Unit): MidiFile {
    val dsl = MiderDSL()
    dsl.block()
    return fromDslInstance(dsl)
}

//@Deprecated(message = "contents will be replaced")
//fun apply(path: String, block: MiderDSL.() -> Any) {
//    val mdsl = MiderDSL()
//    val minimsTicks = 960
//    val clock: Byte = 18
//    with(mdsl, block)
//    val midi = MidiFile()
//    midi.append {
//        track {
//            tempo(mdsl.bpm)
//
//            mdsl.keySignature?.let {
//                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
//            }
//
//            mdsl.timeSignature?.let {
//                meta(MetaEventType.META_TIME_SIGNATURE, it.first.toByte(), log2(it.second.toDouble()).toInt().toByte(), clock, 8)
//            }
//
//            end()
//        }
//
//        track {
//            changeProgram(mdsl.program.id.toByte())
//
//            parseSound(minimsTicks, mdsl.adjustedList())
//
//            end()
//        }
//
//        if (mdsl.otherTracks.isNotEmpty()) {
//            mdsl.otherTracks.forEach {
//                track {
//                    changeProgram(it.program.id.toByte())
//                    parseSound(minimsTicks, it.adjustedList())
//                    end()
//                }
//            }
//        }
//    }
//    midi.save(path)
//}

fun apply(path: String, block: MiderDSL.() -> Unit) {
    if (path.endsWith(".xml")) {
        val dsl = MiderDSL()
        dsl.block()
        Dsl2MusicXml(dsl).save(File(path))
    } else fromDsl(block).save(path)
}

