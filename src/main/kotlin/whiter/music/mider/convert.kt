package whiter.music.mider

import whiter.music.mider.descr.*
import whiter.music.mider.xml.*
import java.util.StringJoiner
import kotlin.math.pow

//fun ppply(path: String, block: MiderDSLv2.() -> Unit) {
//    val dsl = MiderDSLv2()
//    dsl.block()
//    val wholeTicks = 960 * 2 * 2
//    val clock: Byte = 18
//    val midi = MidiFile()
//
//    fun MidiFile.addTrack(dslObj: MiderDSLv2) {
//        track {
//            dslObj.container.mainList
//                .convert2MidiMessages(
//                    wholeTicks
//                )
//                .forEach {
//                    append(it)
//                }
//            end()
//        }
//    }
//
//    midi.append {
//        track {
//            tempo(dsl.bpm)
//
////            dsl.keySignature?.let {
////                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
////            }
//
//            dsl.timeSignature?.let {
//                meta(
//                    MetaEventType.META_TIME_SIGNATURE,
//                    it.first.toByte(),
//                    log2(it.second.toDouble()).toInt().toByte(),
//                    clock, 8
//                )
//            }
//
//            end()
//        }
//
//        addTrack(dsl)
//
//        if (dsl.otherTracks.isNotEmpty()) {
//            dsl.otherTracks.forEach {
//                addTrack(it)
//            }
//        }
//    }
//
//    midi.save(path)
//    val sequencer = MidiSystem.getSequencer()
//    sequencer.setSequence(midi.inStream())
//    sequencer.open()
//    sequencer.start()
//}

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
): MutableList<IMessage> {

    val msgs = mutableListOf<IMessage>()
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

                    // 下行琶音
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

            is InMusicScoreMidiNormalEvent -> {
                modifyChannel = it.channel
                msgs += Message(Event(it.type, it.args, it.channel.toByte()))
            }

            is InMusicScoreMidiMetaEvent -> {
                msgs += MetaMessage(MetaEvent(it.type, it.args))
            }
        }
    }

    return msgs
}

fun List<InMusicScore>.convert2MusicXml(
    tempo: Int = 80,
    beats: Int = 4,
    beatType: Int = 4,
    divisions: Int = 480, // 四分音符所代表的 tick 数字
    keySignature: String? = null
): MusicXml {
    val xmlObj = MusicXml(false)

    xmlObj.part {

        val allMeasureList = mutableListOf<MutableList<NoteElement>>()

        val measureTicks = beats * divisions * beatType / 4 // 一个小节的 tick 数

        var aMeasureCount = 0

        val aMeasureList = mutableListOf<NoteElement>() // 一个小节所包含的音符

        val addLater = mutableListOf<NoteElement>()

        forEachIndexed { index, it ->

            if (addLater.isNotEmpty()) {
                addLater.forEach { ne ->
                    aMeasureList += ne
                    aMeasureCount += ne.duration
                }
                addLater.clear()
            }

            if (aMeasureCount < measureTicks) {
                it.toNoteElement(divisions)?.let { ne ->
                    aMeasureList += ne
                    aMeasureCount += it.durationInDivision(divisions)

                    if (index == lastIndex) {
                        // 列尾, 结束循环
                        allMeasureList += aMeasureList.lightClone()
                        return@forEachIndexed
                    }
                }
            }

            if (aMeasureCount > measureTicks) {
                // 切分
                it.separate(aMeasureCount - measureTicks, divisions)?.let { pair ->
                    aMeasureList.removeLast()

                    aMeasureList += pair.first // pair.second 的 duration 是 aMeasureCount - measureTicks

                    addLater += pair.second

                    allMeasureList += aMeasureList.lightClone()
                    aMeasureList.clear()
                    aMeasureCount = 0
                }
            } else if (aMeasureCount == measureTicks) {
                // 正好凑成一个小节的长度
                allMeasureList += aMeasureList.lightClone()
                aMeasureList.clear()
                aMeasureCount = 0
            }
        }

        if (allMeasureList.size >= 1) {
            val firstMeasure = allMeasureList.removeFirst()

            measure {
                attr(AttributesElement().addDivisions(divisions).let { self ->
                    keySignature?.let { ksef ->
                        val ks = toMusicXmlKeySignature(ksef)
                        self.addKeySignature(ks.first, ks.second)
                    } ?: self.addKeySignature()
                }.addTimeSignature(beats, beatType).addClef())

                direction(DirectionElement(tempo))
                firstMeasure.forEach(::note)
            }

            allMeasureList.forEach { measure ->
                measure {
                    measure.forEach(::note)
                }
            }
        }

    }

    return xmlObj
}

private fun InMusicScore.separate(left: Int, divisions: Int): Pair<MutableList<NoteElement>, MutableList<NoteElement>>? {

    val first = toNoteElement(divisions, durationInDivision(divisions) - left)
    val second = if (this is Note && attach != null && attach?.lyric != null) {
        // 去除第二部分的歌词
        val one = clone()
        one.attach = null
        one.toNoteElement(divisions, left)
    } else toNoteElement(divisions, left)

    if (first == null || second == null) return null

    val n1 = NotationElement()
    n1.addTied("start")

    val n2 = NotationElement()
    n2.addTied("stop")

    if (this !is Rest) {
        // 休止符不需要 tied
        first.forEach { it.addNotation(n1) }
        second.forEach { it.addNotation(n2) }
    }

    return first to second
}

private fun InMusicScore.durationInDivision(divisions: Int) = (duration.value * divisions * 4).toInt()

private fun InMusicScore.toNoteElement(divisions: Int, duration: Int = durationInDivision(divisions)): MutableList<NoteElement>? {
    return when (this) {

        is Note -> {
            mutableListOf(
                autoAlter(duration)
                .setDurationType(divisions)
                .let { self ->
                    if (attach != null && attach?.lyric != null)
                        self.addLyric(attach?.lyric ?: "")
                    self
                }
            )
        }

        is Rest -> {
            val element = NoteElement(duration).setDurationType(divisions)
            element.children.nodes.add(0, Node("rest"))
            mutableListOf(element)
        }

        is Chord -> {
            val root = rootNote.autoAlter(duration).setDurationType(divisions)

            root.let { self ->
                if (attach != null && attach?.lyric != null)
                    self.addLyric(attach?.lyric ?: "")
            }

            val list = mutableListOf(root)

            rest.forEach {
                val chord = it.autoAlter(duration)
                chord.children.nodes.add(0, Node("chord"))
                list += chord
            }

            list
        }

        else -> null
    }
}

private fun Note.autoAlter(givenDuration: Int): NoteElement {

    var aAlter = alter
    if (name.contains("#")) {
        aAlter = 1
    } else if (name.contains("b")) {
        aAlter = -1
    }

    return if (aAlter == 0) {
        NoteElement(name
            .replace("#", "")
            .replace("b", ""),
            pitch, givenDuration)
    } else {
        NoteElement(name
            .replace("#", "")
            .replace("b", ""),
            pitch, givenDuration, aAlter)
    }
}

private fun NoteElement.setDurationType(divisions: Int): NoteElement {
    return when (divisions.toFloat() / duration) {

        // 特殊时值
        480f * 4 / 3 -> addType(DurationType.half) // 二分三连音
        480f * 2 / 3 -> addType(DurationType.quarter) // 四分三连音
        480f / 3 -> addType(DurationType.eighth) // 八分三连音
        480f / 6 -> addType(DurationType.`16th`) // 十六分三连音
        480f / 12 -> addType(DurationType.`32th`) // 三十二分三连音

        // 普通时值
        480f / (1920 * 1.5f) -> {
            addType(DurationType.whole)
            addDot()
        }
        480f / (1920 * 1.5f * 1.5f) -> {
            addType(DurationType.whole)
            addDot().addDot()
        }
        480f / 1920 -> addType(DurationType.whole)

        480f / (960 * 1.5f) -> {
            addType(DurationType.half)
            addDot()
        }
        480f / (960 * 1.5f * 1.5f) -> {
            addType(DurationType.half)
            addDot().addDot()
        }
        480f / 960 -> addType(DurationType.half)

        480f / (240 * 1.5f) -> {
            addType(DurationType.eighth)
            addDot()
        }
        480f / (240 * 1.5f * 1.5f) -> {
            addType(DurationType.eighth)
            addDot().addDot()
        }
        480f / 240 -> addType(DurationType.eighth)

        480f / (120 * 1.5f) -> {
            addType(DurationType.`16th`)
            addDot()
        }
        480f / (120 * 1.5f * 1.5f) -> {
            addType(DurationType.`16th`)
            addDot().addDot()
        }
        480f / 120 -> addType(DurationType.`16th`)

        480f / (60 * 1.5f) -> {
            addType(DurationType.`32th`)
            addDot()
        }
        480f / (60 * 1.5f * 1.5f) -> {
            addType(DurationType.`32th`)
            addDot().addDot()
        }
        480f / 60 -> addType(DurationType.`32th`)

        480f / (30 * 1.5f) -> {
            addType(DurationType.`64th`)
            addDot()
        }
        480f / (30 * 1.5f * 1.5f) -> {
            addType(DurationType.`64th`)
            addDot().addDot()
        }
        480f / 30 -> addType(DurationType.`64th`)

        480f / (15 * 1.5f) -> {
            addType(DurationType.`128th`)
            addDot()
        }
        480f / (15 * 1.5f * 1.5f) -> {
            addType(DurationType.`128th`)
            addDot().addDot()
        }
        480f / 15 -> addType(DurationType.`128th`)

        480f / (480 * 1.5f) -> {
            addType(DurationType.quarter)
            addDot()
        }
        480f / (480 * 1.5f * 1.5f) -> {
            addType(DurationType.quarter)
            addDot().addDot()
        }
        else -> addType(DurationType.quarter)
    }
}

private fun NoteElement.setDuration(describe: DurationDescribe, divisions: Int, tickDuration: Int): NoteElement {

    var type = Node("type", "quarter")

    when (describe.bar) {
        -2 -> type = Node("type", "whole") //addType(DurationType.whole)
        -1 -> type = Node("type", "whole") //addType(DurationType.whole)
         0 -> type = Node("type", "quarter") //addType(DurationType.quarter)
         1 -> type = Node("type", "eighth") //addType(DurationType.eighth)
         2 -> type = Node("type", "16th") //addType(DurationType.`16th`)
         3 -> type = Node("type", "32th") //addType(DurationType.`32th`)
         4 -> type = Node("type", "64th") //addType(DurationType.`64th`)

        else -> {
            if (describe.bar > 0) {
                type = Node("type", "${(2.0.pow(describe.bar + 2)).toInt()}th")
            }

            // 如果是二全音符
        }
    }

    when (tickDuration) {
//        1.0 -> type = Node("type", "whole")
//        0.5 -> type = Node("type", "whole")
//        1.0/4 -> type = Node("type", "quarter")
//        1.0/8 -> type = Node("type", "eighth")
//        1.0/16 -> type = Node("type", "16th")
//        1.0/32 -> type = Node("type", "32th")
//        1.0/64 -> type = Node("type", "64th")
//        1.0/128 -> type = Node("type", "128th")
//        1.0/256 -> type = Node("type", "256th")
    }

    this += type

    for (i in 0 until describe.dot) {
        addDot()
    }

    return this
}

private fun <E> List<E>.lightClone(): MutableList<E> {
    val list = mutableListOf<E>()
    forEach {
        list += it
    }
    return list
}

private fun noteOnMessage(code: Int, duration: Number, velocity: Number, channel: Int = 0): Message {
    return Message(EventType.note_on, byteArrayOf(code.toByte(), velocity.toByte()), time = duration.toInt(), channel = channel.toByte())
}

private fun noteOffMessage(code: Int, duration: Number, velocity: Number, channel: Int = 0): Message {
    return Message(EventType.note_off, byteArrayOf(code.toByte(), velocity.toByte()), time = duration.toInt(), channel = channel.toByte())
}