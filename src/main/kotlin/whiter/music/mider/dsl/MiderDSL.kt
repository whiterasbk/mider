package whiter.music.mider.dsl

import whiter.music.mider.*
import whiter.music.mider.annotation.Tested
import whiter.music.mider.code.MacroConfiguration
import whiter.music.mider.code.toInMusicScoreList
import whiter.music.mider.descr.*
import kotlin.math.pow
import kotlin.reflect.KProperty

class InMusicScoreContainer {
    val mainList: MutableList<InMusicScore> = mutableListOf()
    var currentList: MutableList<InMusicScore> = mainList

    val mainAttach: MutableMap<String, Any> = mutableMapOf()
    var currentAttach: MutableMap<String, Any> = mainAttach

    fun pushIntoContainer(im: InMusicScore) {
        currentList += im
    }

    fun popFromContainer(times: Int = 1): InMusicScore? {
        var last: InMusicScore? = null
        for (i in 0 until times) last = currentList.removeLast()
        return last
    }

//    fun pushNoteIntoContainer(note: Note) {
//
//    }

    operator fun plusAssign(im: InMusicScore) = pushIntoContainer(im)

    operator fun plusAssign(ims: List<InMusicScore>) {
        currentList += ims
    }

    operator fun minus(times: Int): InMusicScore? = popFromContainer(times)

//    operator fun set(pitch: Int, duration: Double, velocity: Int, value: Note) {
//        pushIntoContainer(value)
//    }

    fun addAttach(key: String, any: Any) {
        currentAttach[key] = any
    }

    fun popAttach(key: String): Any? {
        val any = currentAttach[key]
        currentAttach.remove(key)
        return any
    }

    fun redirectTo(to: MutableList<InMusicScore>) {
        currentList = to
    }

    fun redirectToMain() {
            currentList = mainList
    }

    fun redirectAttachTo(to: MutableMap<String, Any>) {
        currentAttach = to
    }

    fun redirectAttachToMain() {
        currentAttach = mainAttach
    }
}

class MiderDSL(
    var dispatcher: NormalChannelDispatcher = NormalChannelDispatcher()
) : DispatcherControlled {

    val container = InMusicScoreContainer()

    val otherTracks = mutableListOf<MiderDSL>()
    private val entrusted = mutableMapOf<String, InMusicScore>()

    var repeatCount: Int = 0

    var volume: Float = 1f // 轨道音量

    val major = 0
    val minor = 1
    /**
     * 大调音阶
     */
    val majorScale = arrayOf(2, 2, 1, 2, 2, 2, 1)
    /**
     * 小调音阶
     */
    val minorScale = arrayOf(2, 1, 2, 2, 1, 2, 2)

    /**
     * 可以用于构建大三和弦, 大七和弦和大九和弦
     * 使用方法: `val symbol by C triad majorChord`
     */
    val majorChord = arrayOf(4, 3, 4, 3)
    /**
     * 可以用于构建小三和弦, 小七和弦, 小九和弦
     * 使用方法: `val symbol by C triad minorChord`
     */
    val minorChord = arrayOf(3, 4, 3, 4)
    // 属七和弦 属大九和弦
    val dominant = arrayOf(4, 3, 3, 4)
    // 属小九和弦
    val dominantMinorNinth = arrayOf(4, 3, 3, 3)
    // 增三和弦
    val augmentedChord = arrayOf(4, 4)
    // 减三和弦 半减七和弦
    val diminiITdChord = arrayOf(3, 3, 4)
    // 减七和弦
    val decreasedSeventh = arrayOf(3, 3, 3)

    var convert2MidiEventConfig = ConvertMidiEventConfiguration()

    var pitch = 4
    var duration = 1.0 / 4 // 1.0为全音符
    var velocity = 100
    var onVelocity = velocity
    var offVelocity = velocity

    var program: MidiInstrument = MidiInstrument.piano
        set(value) {

//            if (!isModifiedProgram) {
//                isModifiedProgram = true
//                channel ++
//            }

            container += InMusicScoreMidiNormalEvent(
                EventType.program_change,
                byteArrayOf(value.id.toByte()),
                dispatcher.getChannel(this)
            )
            field = value
        }
    var bpm = 80
//        set(value) {
//            container += InMusicScoreMidiMetaEvent(
//                MetaEventType.META_TEMPO,
//                bpm(bpm)
//            )
//            field = value
//        }
    var timeSignature: Pair<Int, Int>? = null //= 4 to 4
    var keySignature: String? = null // getKeySignatureFromN(note('C'), major)

    init {
        dispatcher.mount(this)
    }

    val O: Rest get() {
        val rest = Rest(DurationDescribe(default = duration))
        container += rest
        return rest
    }

    val C: Note get() {
        val note = creatNote("C")
        container += note
        return note
    }

    val D: Note get() {
        val note = creatNote("D")
        container += note
        return note
    }

    val E : Note get() {
        val note = creatNote("E")
        container += note
        return note
    }

    val F : Note get() {
        val note = creatNote("F")
        container += note
        return note
    }

    val G : Note get() {
        val note = creatNote("G")
        container += note
        return note
    }

    val A : Note get() {
        val note = creatNote("A")
        container += note
        return note
    }

    val B: Note get() {
        val note = creatNote("B")
        container += note
        return note
    }

    fun hex(array: ByteArray) {
        container += InMusicScoreEvent(array, pitch)
    }

    @JvmName("hexVarg")
    fun hex(vararg byte: Byte) {
        hex(byte)
    }

    fun hex(data: String) {
        container += InMusicScoreEvent(data, pitch)
    }

    private fun creatNote(name: String): Note {
        val note = Note(name, pitch, DurationDescribe(default = duration), velocity)
        if (onVelocity != velocity)
            note.noteOnVelocity = onVelocity
        if (offVelocity != velocity)
            note.noteOffVelocity = offVelocity
        return note
    }

    fun printInserted(function: (InMusicScore) -> Unit = ::println) {
        container.mainList.forEach(function)
    }

    @Tested
    fun track(block: MiderDSL.() -> Unit) {
        val new = MiderDSL(dispatcher)
//        new.channel = channel + 1 // todo
        new.block()
        otherTracks += new
    }

    fun instrument(ins: MidiInstrument, block: MiderDSL.() -> Unit) {
        val cache = program
        program = ins
        block()
        program = cache
    }

    fun instrument(ins: String, block: MiderDSL.() -> Unit) = instrument(MidiInstrument.valueOf(ins), block)

    @Tested
    fun def(block: MiderDSL.() -> Unit): MiderDSL.() -> Unit = block

    @Tested
    fun exec(block: MiderDSL.() -> Unit): MiderDSL.() -> Unit {
        +block
        return block
    }

    @Tested
    operator fun (MiderDSL.() -> Unit).unaryPlus() = this()

    @Tested
    fun velocity(value: Int, block: MiderDSL.() -> Unit) {
        val cacheVelocity = velocity
        velocity = value
        block()
        velocity = cacheVelocity
    }

    fun velocity(on: Int, off: Int, block: MiderDSL.() -> Unit) {
        val cacheOnVelocity = onVelocity
        val cacheOffVelocity = offVelocity
        onVelocity = on
        offVelocity = off
        block()
        onVelocity = cacheOnVelocity
        offVelocity = cacheOffVelocity
    }

    fun onVelocity(value: Int, block: MiderDSL.() -> Unit) {
        val cacheVelocity = onVelocity
        onVelocity = value
        block()
        onVelocity = cacheVelocity
    }

    fun offVelocity(value: Int, block: MiderDSL.() -> Unit) {
        val cacheVelocity = offVelocity
        offVelocity = value
        block()
        offVelocity = cacheVelocity
    }

    @Tested
    fun inserted(block: MiderDSL.() -> Unit): MutableList<InMusicScore> {
        // todo 考虑异步
        val list = mutableListOf<InMusicScore>()
        val cache = container.currentList
        container.redirectTo(list)
        block()
        container.redirectTo(cache)
//        container.redirectToMain()
        return list
    }

    @Tested
    fun repeat(times: Int = 2, block: MiderDSL.() -> Unit) {
        val cache = repeatCount
        for (i in 0 until times) {
            repeatCount = i + 1
            block()
            repeatCount ++
        }
        repeatCount = cache
    }

    /**
     * 配合 String.invoke(isStave: Boolean = true, useMacro: Boolean = true, config: MacroConfiguration) 使用
     */
    @Tested
    fun scope(block: MiderDSL.() -> Unit) {
        val cacheDuration = duration
        val cachePitch = pitch
        val cacheVelocity = velocity
        block()
        velocity = cacheVelocity
        pitch = cachePitch
        duration = cacheDuration
    }

    fun higher(step: Byte = 1, block: MiderDSL.() -> Unit) {
        val inc = pitch + step
        inc(block)
    }

    fun lower(step: Byte = 1, block: MiderDSL.() -> Unit) {
        val dec = pitch - step
        dec(block)
    }

    /**
     * 根据音阶构建和弦
     */
    @Tested
    fun withInterval(int: Int, block: MiderDSL.() -> Unit) {
        inserted(block).forEach {
            container += if (it is Note) {
                val second = it.clone()
                if (int > 0) second.up(int) else if (int < 0) it.down(-int)
                Chord(it, second)
            } else it
        }
    }

    infix fun Int.dot(times: Int) = this * 1.5.pow(times.toDouble())

    val Int.dot get() = this dot 1

    @Tested
    operator fun Int.invoke(block: MiderDSL.() -> Unit) {
        val cachePitch = pitch
        pitch = this
        block()
        pitch = cachePitch
    }

    @Tested
    operator fun Char.invoke(block: MiderDSL.() -> Unit) {
        val cacheDuration = duration
        duration = 1.0 / (this.code - 48)
        block()
        duration = cacheDuration
    }

    operator fun <R> (() -> R).times(times: Int) {
        for (i in 0 until times) this()
    }

    operator fun String.unaryPlus() {
        this()
    }

    //todo
    operator fun String.invoke(isStave: Boolean = true, useMacro: Boolean = true, config: MacroConfiguration = MacroConfiguration()) {
        container += toInMusicScoreList(this, pitch, velocity, onVelocity, offVelocity, duration, isStave, useMacro, config)
    }

    @Tested
    operator fun String.invoke(block: MiderDSL.() -> Unit) {
        // use Mode
        val prefix = if (first() in "+-b#") {
            first().toString()
        } else ""

        val name = (if (first() in "+-b#") {
            substring(1, length)
        } else this)[0].toString()

        val mode = (if (first() in "+-b#") {
            substring(2, length)
        } else substring(1, length))

        val notes = inserted(block)

        when(mode) {
            // 同名小调
            "min", "minor" -> {

                val mapper = minorScaleMapper(prefix + name)
                if (mapper.isEmpty()) {
                    container += notes
                    return
                }

                val matches = mapper.map {
                    it.replace(Regex("[+\\-#b]"), "") to (it.charCount('+', '#') - it.charCount('-', 'b'))
                }

                val mapperNames = matches.map { modeCfg -> modeCfg.first }

                notes.operationExtendNotes {
                    if (!it.isNature && it.actualName in mapperNames) {
                        val alter = matches.find { pair -> it.actualName == pair.first }!!.second
                        if (alter > 0) it.sharp(alter) else if (alter < 0) it.flap(-alter)
                    }
                }

                container += notes
            }

            "maj", "major", "" -> {
                val offset = noteBaseOffset(prefix.replace("+", "#").replace("-", "b") + name)
                if (offset == 0) {
                    container += notes
                    return
                }

                notes.operationExtendNotes {
                    if (!it.isNature) {
                        it.sharp(offset)
                    }
                }

                container += notes
            }

            else -> TODO("not yet implement")
        }
    }

    @Tested
    operator fun <IM: InMusicScore> Int.times(im: IM): IM {
        for (index in 0 until this - 1)
            container += im.clone()
        return im
    }

    @Tested
    infix fun <IM : InMusicScore> IM.dot(times: Int): IM {
        this.duration.points(times)
        return this
    }

    @Tested
    val <IM : InMusicScore> IM.dot get() = this dot 1

    val <IM : InMusicScore> IM.double: IM get() {
        this.duration.double
        return this
    }

    val <IM : InMusicScore> IM.halve: IM get() {
        this.duration.halve
        return this
    }

    @Tested
    operator fun <IM : InMusicScore> IM.times(value: Number): IM {
        this.duration.multiple = value.toDouble()
        return this
    }

    @Tested
    operator fun <IM : InMusicScore> IM.div(value: Number): IM {
        this.duration.denominator = value.toDouble()
        return this
    }

    @Tested
    operator fun <NC : NoteContainer> NC.plus(note: Note): NC {
        container - 1
        this += note
        return this
    }

    @Tested
    operator fun <NC : NoteContainer> NC.minus(note: Note): NC {
        container - 1
        this -= note
        return this
    }

    @Tested
    operator fun <IM: InMusicScore> IM.getValue(nothing: Nothing?, property: KProperty<*>): IM {
        val im: IM = (entrusted[property.name]?.let {
            val clone = it.clone()
            container += clone
            clone
        } ?: throw Exception("id ${property.name} is miss match")) as IM

        return im
    }

    @Tested
    infix fun <IM: InMusicScore> IM.into(id: String): IM {
        entrusted[id] = this.clone()
        container - 1
        return this
    }

    operator fun <IM> Rest.plus(im: IM): IM = im

    @Tested
    infix fun Glissando.gliss(note: Note): Glissando = this + note

    @Tested
    val Glissando.wave: Glissando get() {
        isWave = true
        return this
    }

    val Glissando.hasBlack: Glissando get() {
        isContainBlack = true
        return this
    }

    @Tested
    val Appoggiatura.back: Appoggiatura get() {
        isFront = false
        return this
    }

    @Tested
    operator fun Chord.plus(pitch: Int): Chord {
        notes.forEach { it += pitch }
        return this
    }

    @Tested
    operator fun Chord.minus(pitch: Int): Chord {
        notes.forEach { it -= pitch }
        return this
    }

    @Tested
    operator fun Chord.div(note: Note): Chord {

        container - 1

        if (note.actualCode % 12 !in notes.map { it.actualCode % 12 })
            throw Exception("given note: ${note.name} not in $this")

        val target = notes.find { note.actualCode % 12 == it.actualCode % 12 }
        target?.let { b ->
            notes.forEach {
                if (it.actualCode < b.actualCode) {
                    it.pitch ++
                }
            }
        } ?: throw Exception("given note: ${note.actualName} not in $this")

        return this
    }

    infix fun Chord.inverse(note: Note): Chord = this / note

    val Chord.sus4: Chord get() {

        when(notes[1].actualCode - rootNote.actualCode) {
            4 -> notes[1].sharp()
            3 -> notes[1].sharp(2)
            else -> {
                throw Exception("this chord did not contain a three degree note from root")
            }
        }
        return this
    }

    @Tested
    val Chord.sus: Chord get() = sus4

    @Tested
    val Chord.sus2: Chord get() {

        when(notes[1].actualCode - rootNote.actualCode) {
            4 -> notes[1].flap(2)
            3 -> notes[1].flap()
            else -> {
                throw Exception("this chord did not contain a three degree note from root")
            }
        }

        return this
    }

    @Tested
    val Chord.ascending: Chord get() {
        this.arpeggio = ArpeggioType.Ascending
        return this
    }

    @Tested
    val Chord.downward: Chord get() {
        this.arpeggio = ArpeggioType.Downward
        return this
    }

    @Tested
    operator fun Note.plus(pitch: Int): Note {
        this += pitch
        return this
    }

    @Tested
    operator fun Note.minus(pitch: Int): Note {
        this -= pitch
        return this
    }

    operator fun Note.minus(note: Note): Int {
        val interval = this.actualCode - note.actualCode
        container - 2
        return interval
    }

    @Tested
    operator fun Note.not(): Note {
        this.isNature = true
        return this
    }

    @Tested
    operator fun Note.get(pitch: Int, multiple: Number = .25): Note {
        this.pitch = pitch
        this.duration.default = multiple.toDouble()
        return this
    }

    @Tested
    operator fun Note.get(lyric: String): Note {
        val na = NoteAttach()
        na.lyric = lyric
        attach = na
        return this
    }

    @Tested
    operator fun Note.unaryPlus(): Note {
        sharp()
        return this
    }

    @Tested
    operator fun Note.unaryMinus(): Note {
        flap()
        return this
    }

    @Tested
    operator fun Note.rem(value: Int): Note {
        this.velocity = value
        return this
    }

    @Tested
    operator fun Note.plus(note: Note): Chord {
        val chord = Chord(this, note)
        container - 2
        container += chord
        return chord
    }

    @Tested
    operator fun Note.rangeTo(note: Note): Scale {
        val scale = Scale.generate(this, note)
        container - 2
        container += scale
        return scale
    }

    @Tested
    infix fun Note.triad(mode: Array<Int>): Chord {
        val second = clone()
        second.up(mode[0])
        val third = second.clone()
        third.up(mode[1])

        val chord = Chord(this, second, third)
        container - 1
        container += chord
        return chord
    }

    @Tested
    infix fun Note.seventh(mode: Array<Int>): Chord {
        val tr = this triad mode
        val fourth = tr.last().clone()
        fourth.up(mode[2])
        tr += fourth
        return tr
    }

    @Tested
    infix fun Note.add9(mode: Array<Int>): Chord {
        val tr = this triad mode
        val fourth = tr.last().clone()
        fourth.up(mode[2] + mode[3])
        tr += fourth
        return tr
    }

    @Tested
    infix fun Note.ninths(mode: Array<Int>): Chord {
        val se = this seventh mode
        val fifth = se.last().clone()
        fifth.up(mode[3])
        se += fifth
        return se
    }

    @Tested
    infix fun Note.gliss(note: Note): Glissando {
        val gliss = Glissando(this, note)
        container - 2
        container += gliss
        return gliss
    }

    @Tested
    infix fun Note.appoggiatura(note: Note): Appoggiatura {
        val app = Appoggiatura(this, note)
        container - 2
        container += app
        return app
    }

    override fun toString(): String = "大弦嘈嘈如急雨，小弦切切如私语。嘈嘈切切错杂弹，大珠小珠落玉盘。"
}