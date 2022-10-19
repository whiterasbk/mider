package whiter.music.mider

import whiter.music.mider.descr.*
import whiter.music.mider.xml.*

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
 * @param volume 轨道主音量
 */
fun List<InMusicScore>.convert2MidiMessages(
    wholeTicks: Int,
    channel: Int = 0,
    volume: Float = 1f,
    config: ConvertMidiEventConfiguration = ConvertMidiEventConfiguration()
): MutableList<IMessage> {

    val msgs = mutableListOf<IMessage>()
    var previousTicks = 0L
    var modifyChannel = channel

    fun Note.getRealChannel() = attach?.channel ?: modifyChannel
    fun Note.getRealPreviousTicks() = previousTicks.let { pt ->
        if (pt == 0L) {
            attach?.gap?.calcTicks(wholeTicks.toLong()) ?: 0L
        } else pt
    } //attach?.gap?.let { it.calcTicks(wholeTicks.toLong()) + previousTicks } ?: previousTicks
    fun Note.getDurationTicks() = duration.value * wholeTicks
    fun Note.onVelocity() = noteOnVelocity * volume
    fun Note.offVelocity() = noteOffVelocity * volume

    fun checkList(list: MutableList<Note>) {
        val first = list.removeFirst()
        msgs += noteOnMessage(first.actualCode, first.getDurationTicks(), first.noteOnVelocity * volume, first.getRealChannel())
        msgs += noteOffMessage(first.actualCode, first.duration.value * wholeTicks, first.noteOffVelocity  * volume, first.getRealChannel())

        list.forEach { note ->
            msgs += noteOnMessage(note.actualCode, 0, note.noteOnVelocity * volume, note.getRealChannel())
            msgs += noteOffMessage(note.actualCode, note.duration.value * wholeTicks, note.noteOffVelocity  * volume, note.getRealChannel())
        }
    }

    forEach {
        when (it) {
            is Note -> {
                msgs += with(it) {
                    noteOnMessage(actualCode, getRealPreviousTicks(), onVelocity(), getRealChannel())
                }

                msgs += with(it) {
                    noteOffMessage(actualCode, getDurationTicks(), offVelocity(), getRealChannel())
                }
                previousTicks = 0
            }

            is Chord -> {

                when (it.arpeggio) {
                    // 上行琶音
                    ArpeggioType.Ascending -> {

                        msgs += with(it.rootNote) {
                            noteOnMessage(actualCode, getRealPreviousTicks(), onVelocity(), getRealChannel())
                        }

//                        msgs += noteOnMessage(it.rootNote.actualCode, previousTicks, it.rootNote.noteOnVelocity  * volume, it.rootNote.getRealChannel())
                        var count = 0

                        it.rest.forEach { note ->
                            msgs += with(note) {
                                noteOnMessage(
                                    actualCode,
                                    (++count) * config.arpeggioIntervalDuration * wholeTicks,
                                    onVelocity(),
                                    getRealChannel())
                            }
                        }

                        msgs += noteOffMessage(
                            it.rootNote.actualCode,
                            it.rootNote.duration.value * wholeTicks - (count) * config.arpeggioIntervalDuration * wholeTicks,
                            it.rootNote.noteOffVelocity * volume,
                            it.rootNote.getRealChannel())

                        it.rest.forEach { note ->
                            msgs += noteOffMessage(note.actualCode, 0, note.noteOffVelocity  * volume, note.getRealChannel())
                        }
                    }

                    // 下行琶音
                    ArpeggioType.Downward -> {
                        msgs += noteOnMessage(it.rest.last().actualCode, it.rest.last().getRealPreviousTicks(), it.rest.last().noteOnVelocity  * volume, it.rest.last().getRealChannel())

                        var count = 0
                        val tpList = mutableListOf<Note>()
                        tpList += it.rest.reversed()
                        tpList.removeFirst()
                        tpList += it.rootNote

                        tpList.forEach { note ->
                            msgs += noteOnMessage(
                                note.actualCode,
                                (++count) * config.arpeggioIntervalDuration * wholeTicks,
                                note.noteOnVelocity * volume,
                                note.getRealChannel())
                        }

                        msgs += noteOffMessage(
                            it.rest.last().actualCode,
                            it.rest.last().duration.value * wholeTicks - (count) * config.arpeggioIntervalDuration * wholeTicks,
                            it.rest.last().noteOffVelocity * volume,
                            it.rest.last().getRealChannel())

                        tpList.forEach { note ->
                            msgs += noteOffMessage(note.actualCode, 0, note.noteOffVelocity  * volume, note.getRealChannel())
                        }
                    }

                    else -> {
                        msgs += noteOnMessage(it.rootNote.actualCode, it.rootNote.getRealPreviousTicks(), it.rootNote.noteOnVelocity  * volume, it.rootNote.getRealChannel())
                        it.rest.forEach { note ->
                            msgs += noteOnMessage(note.actualCode, 0, note.noteOnVelocity  * volume, note.getRealChannel())
                        }

                        if (it.isIndependentDuration) {
                            // 找出时值最短的, 最长的便是 和弦时值
                            val notes = it.clone().notes
                            notes.sortWith { n1, n2 ->
                                (480 * (n1.duration.value - n2.duration.value)).toInt()
                            }

                            var previousCount = 0
                            notes.forEach { n ->
                                val duration = (n.duration.value * wholeTicks - previousCount).toInt()
                                msgs += noteOffMessage(n.actualCode, duration, n.noteOffVelocity  * volume, n.getRealChannel())
                                previousCount += duration
                            }

                        } else {
                            msgs += noteOffMessage(it.rootNote.actualCode, it.rootNote.duration.value * wholeTicks, it.rootNote.noteOffVelocity * volume, it.rootNote.getRealChannel())
                            it.rest.forEach { note ->
                                msgs += noteOffMessage(note.actualCode, 0, note.noteOffVelocity  * volume, note.getRealChannel())
                            }
                        }
                    }
                }

                previousTicks = 0
            }

            is Scale -> {
                checkList(it.clone().notes)
                previousTicks = 0
            }

            is TieNote -> {
                msgs += noteOnMessage(it.main.actualCode, it.main.getRealPreviousTicks(), it.main.noteOnVelocity * volume, it.main.getRealChannel())
                msgs += noteOffMessage(it.main.actualCode, it.duration.value * wholeTicks, it.main.noteOffVelocity  * volume, it.main.getRealChannel())
                previousTicks = 0
            }

            is Rest -> {
                previousTicks += (it.duration.value * wholeTicks).toInt()
            }

            is Appoggiatura -> {

                if (it.isFront) {
                    msgs += noteOnMessage(it.second.actualCode, it.second.getRealPreviousTicks(), it.second.noteOnVelocity * volume, it.second.getRealChannel())
                    msgs += noteOffMessage(it.second.actualCode, config.appoggiaturaDuration * wholeTicks, it.second.noteOffVelocity * volume, it.second.getRealChannel())

                    msgs += noteOnMessage(it.main.actualCode, 0, it.main.noteOnVelocity * volume, it.main.getRealChannel())
                    msgs += noteOffMessage(it.main.actualCode, (it.main.duration.value - config.appoggiaturaDuration) * wholeTicks, it.main.noteOffVelocity * volume, it.main.getRealChannel())

                } else {
                    msgs += noteOnMessage(it.second.actualCode, it.second.getRealPreviousTicks(), it.second.noteOnVelocity * volume, it.second.getRealChannel())
                    msgs += noteOffMessage(it.second.actualCode, (it.second.duration.value - config.appoggiaturaDuration) * wholeTicks, it.second.noteOffVelocity * volume, it.second.getRealChannel())

                    msgs += noteOnMessage(it.main.actualCode, 0, it.main.noteOnVelocity * volume, it.main.getRealChannel())
                    msgs += noteOffMessage(it.main.actualCode, config.appoggiaturaDuration * wholeTicks, it.main.noteOffVelocity * volume, it.main.getRealChannel())
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

            is InMusicScoreEvent -> msgs += HexMessage(it.getHex(wholeTicks))
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

        is TieNote -> {
            mutableListOf(
                autoAlter(duration)
                    .setDurationType(divisions)
                    .let { self ->
                        if (main.attach != null && main.attach?.lyric != null)
                            self.addLyric(main.attach?.lyric ?: "")
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

private fun TieNote.autoAlter(givenDuration: Int): NoteElement = main.autoAlter(givenDuration)


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