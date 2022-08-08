package whiter.music.mider

import whiter.music.mider.descr.*
import whiter.music.mider.dsl.MiderDSLv2
import javax.sound.midi.MidiSystem
import kotlin.math.log2

fun ppply(path: String, block: MiderDSLv2.() -> Unit) {
    val dsl = MiderDSLv2()
    dsl.block()
    val wholeTicks = 960 * 2 * 2
    val clock: Byte = 18
    val midi = MidiFile()

    fun MidiFile.addTrack(dslObj: MiderDSLv2) {
        track {
            dslObj.container.mainList
                .convert2MidiMessages(
                    wholeTicks
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

    midi.save(path)
    val sequencer = MidiSystem.getSequencer()
    sequencer.setSequence(midi.inStream())
    sequencer.open()
    sequencer.start()
}

class ConvertMidiEventConfiguration {
    /**
     * 琶音各组成音的时间间隔
     */
    val arpeggioIntervalDuration: Double = 1.0 / 32

    /**
     * 倚音时值
     */
    val appoggiaturaDuration: Double = 1.0 / 32
}

/**
 * @param velocity 轨道主音量
 */
fun List<InMusicScore>.convert2MidiMessages(
    wholeTicks: Int,
    channel: Int = 0,
    velocity: Float = 1f,
    config: ConvertMidiEventConfiguration = ConvertMidiEventConfiguration()
): MutableList<Message> {

    val msgs = mutableListOf<Message>()
    var previousTicks = 0
    var modifyChannel = channel

    fun checkList(list: MutableList<Note>) {
        val first = list.removeFirst()

        msgs += noteOnMessage(first.actualCode, previousTicks, first.velocity * velocity, modifyChannel)
        msgs += noteOffMessage(first.actualCode, first.duration.value * wholeTicks, first.velocity  * velocity, modifyChannel)

        list.forEach { note ->
            msgs += noteOnMessage(note.actualCode, 0, note.velocity * velocity, modifyChannel)
            msgs += noteOffMessage(note.actualCode, note.duration.value * wholeTicks, note.velocity  * velocity, modifyChannel)
        }
    }

    forEach {

        when (it) {
            is Note -> {
                msgs += noteOnMessage(it.actualCode, previousTicks, it.velocity * velocity, modifyChannel)
                msgs += noteOffMessage(it.actualCode, it.duration.value * wholeTicks, it.velocity  * velocity, modifyChannel)
                previousTicks = 0
            }

            is Chord -> {

                when (it.arpeggio) {
                    // 上行琶音
                    ArpeggioType.Ascending -> {
                        msgs += noteOnMessage(it.rootNote.actualCode, previousTicks, it.rootNote.velocity  * velocity, modifyChannel)
                        var count = 0

                        it.rest.forEach { note ->
                            msgs += noteOnMessage(
                                note.actualCode,
                                (++count) * config.arpeggioIntervalDuration * wholeTicks,
                                note.velocity * velocity,
                                 modifyChannel)
                        }

                        msgs += noteOffMessage(
                            it.rootNote.actualCode,
                            it.rootNote.duration.value * wholeTicks - (count) * config.arpeggioIntervalDuration * wholeTicks,
                            it.rootNote.velocity * velocity,
                            modifyChannel)

                        it.rest.forEach { note ->
                            msgs += noteOffMessage(note.actualCode, 0, note.velocity  * velocity, modifyChannel)
                        }
                    }

                    // 琶音下行
                    ArpeggioType.Downward -> {
                        msgs += noteOnMessage(it.rest.last().actualCode, previousTicks, it.rest.last().velocity  * velocity, modifyChannel)

                        var count = 0
                        val tpList = mutableListOf<Note>()
                        tpList += it.rest.reversed()
                        tpList.removeFirst()
                        tpList += it.rootNote

                        tpList.forEach { note ->
                            msgs += noteOnMessage(
                                note.actualCode,
                                (++count) * config.arpeggioIntervalDuration * wholeTicks,
                                note.velocity * velocity,
                                modifyChannel)
                        }

                        msgs += noteOffMessage(
                            it.rest.last().actualCode,
                            it.rest.last().duration.value * wholeTicks - (count) * config.arpeggioIntervalDuration * wholeTicks,
                            it.rest.last().velocity * velocity,
                            modifyChannel)

                        tpList.forEach { note ->
                            msgs += noteOffMessage(note.actualCode, 0, note.velocity  * velocity, modifyChannel)
                        }
                    }

                    else -> {
                        msgs += noteOnMessage(it.rootNote.actualCode, previousTicks, it.rootNote.velocity  * velocity, modifyChannel)
                        it.rest.forEach { note ->
                            msgs += noteOnMessage(note.actualCode, 0, note.velocity  * velocity, modifyChannel)
                        }

                        msgs += noteOffMessage(it.rootNote.actualCode, it.rootNote.duration.value * wholeTicks, it.rootNote.velocity * velocity, modifyChannel)
                        it.rest.forEach { note ->
                            msgs += noteOffMessage(note.actualCode, 0, note.velocity  * velocity, modifyChannel)
                        }
                    }
                }

                previousTicks = 0
            }

            is Scale -> {

//                val allList = it.clone().notes
                checkList(it.clone().notes)
//                val first = allList.removeFirst()
//
//                msgs += noteOnMessage(first.actualCode, previousTicks, first.velocity * velocity, modifyChannel)
//                msgs += noteOffMessage(first.actualCode, first.duration.value * wholeTicks, first.velocity  * velocity, modifyChannel)
//
//                allList.forEach { note ->
//                    msgs += noteOnMessage(note.actualCode, 0, note.velocity * velocity, modifyChannel)
//                    msgs += noteOffMessage(note.actualCode, note.duration.value * wholeTicks, note.velocity  * velocity, modifyChannel)
//                }

//                it.notes.forEach { note ->
//                    msgs += noteOnMessage(note.actualCode, previousTicks, note.velocity * velocity, modifyChannel)
//                    msgs += noteOffMessage(note.actualCode, note.duration.value * wholeTicks, note.velocity  * velocity, modifyChannel)
//                }

                previousTicks = 0
            }

            is Rest -> {
                previousTicks += (it.duration.value * wholeTicks).toInt()
            }

            is Appoggiatura -> {

                if (it.isFront) {
                    msgs += noteOnMessage(it.second.actualCode, previousTicks, it.second.velocity * velocity, modifyChannel)
                    msgs += noteOffMessage(it.second.actualCode, config.appoggiaturaDuration * wholeTicks, it.second.velocity * velocity, modifyChannel)

                    msgs += noteOnMessage(it.main.actualCode, 0, it.main.velocity * velocity, modifyChannel)
                    msgs += noteOffMessage(it.main.actualCode, (it.main.duration.value - config.appoggiaturaDuration) * wholeTicks, it.main.velocity * velocity, modifyChannel)

                } else {
                    msgs += noteOnMessage(it.second.actualCode, previousTicks, it.second.velocity * velocity, modifyChannel)
                    msgs += noteOffMessage(it.second.actualCode, (it.second.duration.value - config.appoggiaturaDuration) * wholeTicks, it.second.velocity * velocity, modifyChannel)

                    msgs += noteOnMessage(it.main.actualCode, 0, it.main.velocity * velocity, modifyChannel)
                    msgs += noteOffMessage(it.main.actualCode, config.appoggiaturaDuration * wholeTicks, it.main.velocity * velocity, modifyChannel)
                }

                previousTicks = 0
            }

            is Glissando -> {

                val allList = mutableListOf<Note>()

                if (it.isWave) {
                    // todo 波形滑音
                } else {
                    if (it.isContainBlack) {
                        it.notes.glissandoPoints().forEach { pair ->
                            val first = pair.first.clone()
                            first.duration += pair.second.duration
                            val countScale = Scale.generate(first, pair.second)
                            val notesCount = countScale.notes.size
                            val duration = DurationDescribe(default = first.duration.value)
                            duration.denominator = notesCount.toDouble()
                            val rScale = Scale.generate(first, pair.second, duration)
                            val notes = rScale.notes
                            allList += notes
                        }
                    } else {
                        it.notes.glissandoPoints().forEach { pair ->
                            val first = pair.first.clone()
                            first.duration += pair.second.duration
                            val countScale = Scale.generate(first, pair.second)
                            countScale under arrayOf(2, 2, 1, 2, 2, 2, 1)
                            val notesCount = countScale.notes.size
                            val duration = DurationDescribe(default = first.duration.value)
                            duration.denominator = notesCount.toDouble()
                            val rScale = Scale.generate(first, pair.second, duration)
                            rScale under arrayOf(2, 2, 1, 2, 2, 2, 1)
                            val notes = rScale.notes
                            allList += notes
                        }
                    }

                    checkList(allList)

//                    val first = allList.removeFirst()
//
//                    msgs += noteOnMessage(first.actualCode, previousTicks, first.velocity * velocity, modifyChannel)
//                    msgs += noteOffMessage(first.actualCode, first.duration.value * wholeTicks, first.velocity  * velocity, modifyChannel)
//
//                    allList.forEach { note ->
//                        msgs += noteOnMessage(note.actualCode, 0, note.velocity * velocity, modifyChannel)
//                        msgs += noteOffMessage(note.actualCode, note.duration.value * wholeTicks, note.velocity * velocity, modifyChannel)
//                    }



//                    for (i in 1 until it.notes.size) {
//                        msgs += noteOnMessage(it.notes[0].actualCode, previousTicks, it.notes[0].velocity * velocity, modifyChannel)
//                        msgs += noteOffMessage(it.notes[0].actualCode, it.notes[0].duration.value * wholeTicks, it.notes[0].velocity  * velocity, modifyChannel)
//                    }
                }

                previousTicks = 0
            }

            is InMusicScoreMidiEvent -> {
                modifyChannel = it.channel
                msgs += Message(Event(it.type, it.args, it.channel.toByte()))
            }
        }
    }

    return msgs
}

private fun noteOnMessage(code: Int, duration: Number, velocity: Number, channel: Int = 0): Message {
    return Message(EventType.note_on, byteArrayOf(code.toByte(), velocity.toByte()), time = duration.toInt(), channel = channel.toByte())
}

private fun noteOffMessage(code: Int, duration: Number, velocity: Number, channel: Int = 0): Message {
    return Message(EventType.note_off, byteArrayOf(code.toByte(), velocity.toByte()), time = duration.toInt(), channel = channel.toByte())
}