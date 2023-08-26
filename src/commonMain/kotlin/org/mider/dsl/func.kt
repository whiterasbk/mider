package org.mider.dsl

import org.mider.MetaEventType
import org.mider.MidiFile
import org.mider.convert2MidiMessages
import org.mider.convert2MusicXml
import org.mider.descr.InMusicScore
import org.mider.xml.MusicXml
//import java.io.File
//import java.util.*
//import javax.sound.midi.MidiSystem
//import javax.sound.midi.Sequencer
//import kotlin.concurrent.timerTask
import kotlin.math.log2

fun fromDslInstance(dsl: MiderDSL): MidiFile {
    val wholeTicks = 960 * 2 * 2
    val clock: Byte = 18
    val midi = MidiFile()

    fun MidiFile.addTrack(dslObj: MiderDSL) {
        track {
            dslObj.container.mainList
                .convert2MidiMessages(
                    wholeTicks,
                    volume = dslObj.volume,
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

fun dsl2MusicXml(dsl: MiderDSL, divisions: Int = 480): MusicXml {
    // todo 将 track 和 part 对应
    val allTracks = mutableListOf<InMusicScore>()
    allTracks += dsl.container.mainList
    dsl.otherTracks.forEach {
        allTracks += it.container.mainList
    }

    return allTracks.convert2MusicXml(dsl.bpm,
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

