package whiter.music.mider.dsl

import whiter.music.mider.annotation.Tested
import whiter.music.mider.code.MacroConfiguration
import whiter.music.mider.code.toInMusicScoreList
import whiter.music.mider.descr.*
import kotlin.math.pow
import kotlin.reflect.KProperty

class InMusicScoreContainer {
    val mainList: MutableList<InMusicScore> = mutableListOf()
    private var currentList: MutableList<InMusicScore> = mainList

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

    fun redirectTo(to: MutableList<InMusicScore>) {
        currentList = to
    }

    fun redirectToMain() {
            currentList = mainList
    }

}

class MiderDSLv2 {
    val container = InMusicScoreContainer()

    val otherTracks = mutableListOf<MiderDSLv2>()
    private val entrustedChord = mutableMapOf<String, Chord>()
    private val entrustedNote = mutableMapOf<String, Note>()
    private val entrusted = mutableMapOf<String, InMusicScore>()

//    private val entrusti = mutableMapOf<String, note>()
//    private val entrustc = mutableMapOf<String, MutableList<note>>()
//    private val __rest_instance = rest()

//    private val current: InMusicScore get() = container.mainList.last()
//    private val last: InMusicScore get() = container.mainList[container.mainList.lastIndex - 1]

    private var isInRepeat: Boolean = false
    private var repeatCount: Int = 0
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

    var pitch = 4
    var duration = 1.0/4.0 // 1.0为全音符
    var defaultNoteDuration = 4 // 默认是四分音符
    var velocity = 100
//    var program: instrument = instrument.piano
    var bpm = 80
    var timeSignature: Pair<Int, Int>? = null //= 4 to 4
//    val signatureKeysList = mutableListOf<Pair<Pair<Ks, Int>, IntRange>>()
//    var keySignature: Pair<Ks, Int>? = null // getKeySignatureFromN(note('C'), major)
    val end = 0

    val O: Rest get() {
        val rest = Rest(InMusicScore.DurationDescribe(default = duration))
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

    private fun creatNote(name: String): Note = Note(name, pitch, InMusicScore.DurationDescribe(default = duration), velocity)

    fun printInserted(function: (InMusicScore) -> Unit = ::println) {
        container.mainList.forEach(function)
    }

    fun velocity(value: Int, block: MiderDSLv2.() -> Unit) {
        val cacheVelocity = velocity
        velocity = value
        block()
        velocity = cacheVelocity
    }

    fun inserted(block: MiderDSLv2.() -> Unit): MutableList<InMusicScore> {
        val list = mutableListOf<InMusicScore>()
        container.redirectTo(list)
        block()
        container.redirectToMain()
        return list
    }

    fun repeat(times: Int = 2, block: MiderDSLv2.() -> Unit) {
        for (i in 0 until times) block()
    }

    // 配合 String.invoke(isStave: Boolean = true, useMacro: Boolean = true, config: MacroConfiguration) 使用
    fun scope(block: MiderDSLv2.() -> Unit) {
        val cacheDuration = duration
        val cachePitch = pitch
        val cacheVelocity = velocity
        block()
        velocity = cacheVelocity
        pitch = cachePitch
        duration = cacheDuration
    }

//    operator fun (MiderDSLv2.() -> Unit).unaryPlus() {
//        this()
//    }
    infix fun Int.dot(times: Int) = this * 1.5.pow(times.toDouble())

    val Int.dot get() = this dot 1

    operator fun Int.invoke(block: MiderDSLv2.() -> Unit) {
        val cachePitch = pitch
        pitch = this
        block()
        pitch = cachePitch
    }

    operator fun Int.times(note: Note): Note {
        for (index in 0 until this - 1)
            container += note.clone()
        return note
    }

    operator fun Int.times(chord: Chord): Chord {
        for (index in 0 until this - 1)
            container += chord.clone()
        return chord
    }

    operator fun Char.invoke(block: MiderDSLv2.() -> Unit) {
        val cacheDuration = duration
        duration = 1.0 / (this.code - 48)
        block()
        duration = cacheDuration
    }

    operator fun <R> (() -> R).times(times: Int) {
        for (i in 0 until times) this()
    }

    operator fun String.unaryPlus() {
//        container += toInMusicScoreList(this, pitch, velocity, duration)
        this()
    }

    operator fun String.invoke(isStave: Boolean = true, useMacro: Boolean = true, config: MacroConfiguration = MacroConfiguration()) {
        container += toInMusicScoreList(this, pitch, velocity, duration, isStave, useMacro, config)
    }

    operator fun String.invoke(block: MiderDSLv2.() -> Unit) {
        // use Mode
    }

//    operator fun Rest.times(value: Number): Rest {
//        duration.multiple = value.toDouble()
//        return this
//    }
//
//    operator fun Rest.div(value: Number): Rest {
//        duration.denominator = value.toDouble()
//        return this
//    }

    @Tested
    infix fun <IM : InMusicScore> IM.dot(times: Int): IM {
        this.duration.points(times)
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

//    @Tested
//    operator fun Glissando.plus(note: Note): Glissando {
//        container - 1
//        this += note
//        return this
//    }

    @Tested
    infix fun Glissando.gliss(note: Note): Glissando = this + note

    @Tested
    val Glissando.wave: Glissando get() {
        isWave = true
        return this
    }

    @Tested
    val Appoggiatura.back: Appoggiatura get() {
        isFront = false
        return this
    }

//    @Tested
//    operator fun Chord.plus(note: Note): Chord {
//        container - 1
//        this += note
//        return this
//    }

    @Tested
    operator fun Chord.plus(pitch: Int): Chord {
        notes.forEach { it += pitch }
        return this
    }

//    @Tested
//    operator fun Chord.minus(note: Note): Chord {
//        container - 1
//        this -= note
//        return this
//    }

    @Tested
    operator fun Chord.minus(pitch: Int): Chord {
        notes.forEach { it -= pitch }
        return this
    }

//    @Tested
//    operator fun Chord.times(value: Number): Chord {
//        this.duration.multiple = value.toDouble()
//        return this
//    }
//
//    operator fun Chord.div(value: Number): Chord {
//        this.duration.denominator = value.toDouble()
//        return this
//    }

    @Tested
    operator fun Chord.div(note: Note): Chord {

        container - 1

        if (note.code % 12 !in notes.map { it.code % 12 })
            throw Exception("given note: ${note.name} not in $this")

        val target = notes.find { note.code % 12 == it.code % 12 }
        target?.let { b ->
            notes.forEach {
                if (it.code < b.code) {
                    it.pitch ++
                }
            }
        } ?: throw Exception("given note: ${note.name} not in $this")

        return this
    }

    infix fun Chord.inverse(note: Note): Chord = this / note

    val Chord.sus4: Chord get() {

        when(notes[1].code - rootNote.code) {
            4 -> notes[1].code ++
            3 -> notes[1].code += 2
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

        when(notes[1].code - rootNote.code) {
            4 -> notes[1].code -= 2
            3 -> notes[1].code --
            else -> {
                throw Exception("this chord did not contain a three degree note from root")
            }
        }

        return this
    }

//    @Tested
//    operator fun Chord.getValue(nothing: Nothing?, property: KProperty<*>): Chord {
//        return entrustedChord[property.name]?.let {
//            val clone = it.clone()
//            container += clone
//            clone
//        } ?: throw Exception("id ${property.name} is miss match")
//    }

//    @Tested
//    infix fun Chord.into(id: String): Chord {
//        entrustedChord[id] = this.clone()
//        container - 1
//        return this
//    }

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
    val Note.dot: Note get() = this dot 1

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

//    @Tested
//    operator fun Note.getValue(nothing: Nothing?, property: KProperty<*>): Note {
//        return entrustedNote[property.name]?.let {
//            val clone = it.clone()
//            container += clone
//            clone
//        } ?: throw Exception("id ${property.name} is miss match")
//    }

    @Tested
    operator fun Note.rangeTo(note: Note): Scale {
        val scale = Scale.generate(this, note)
        container - 2
        container += scale
        return scale
    }

//    @Tested
//    infix fun Note.into(id: String): Note {
//        entrustedNote[id] = this.clone()
//        container - 1
//        return this
//    }

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