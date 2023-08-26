package org.mider.descr

import org.mider.impl.Cloneable
import org.mider.noteBaseOffset
import org.mider.noteNameFromCode
import org.mider.noteNameFromCodeFlat
import org.mider.nextNoteIntervalInMajorScale
import org.mider.previousNoteIntervalInMajorScale

/**
 * 描述一个音符, 信息更全面
 * @param code 音符代码 0~127
 * @param alter 正数为升负数为降
 */
class Note(
    /**
     * 获取 midi code, 但是不受 alter 影响, 获取受 alter 影响后的 code 请使用 actualCode
     * @see actualCode
     */
    var code: Int,
    override val duration: DurationDescribe = DurationDescribe(),
    velocity: Int = 100,
    var isNature: Boolean = false, // 是否添加了还原符号
    var alter: Int = 0,
    private var innerAttach: NoteAttach? = null
) : InMusicScore, HasFlatAndSharp, HasOctave, CanModifyTargetVelocity, CanModifyTargetPitch {

    constructor(name: String, pitch: Int = 4, duration: DurationDescribe = DurationDescribe(), velocity: Int = 100)
            : this(noteBaseOffset (name) + (pitch + 1) * 12, duration, velocity)
    constructor(name: Char, pitch: Int = 4, duration: DurationDescribe = DurationDescribe(), velocity: Int = 100)
            : this(name.uppercase(), pitch, duration, velocity)

    var velocity: Int = velocity
        set(value) {
            noteOnVelocity = value
            noteOffVelocity = value
            field = value
        }

    var noteOnVelocity = velocity
    var noteOffVelocity = velocity

    var attach: NoteAttach? get() {
        return innerAttach
    } set(value) {
        value?.let { passing ->
            innerAttach?.copy(passing) ?: run {
                innerAttach = NoteAttach().apply { copy(passing) }
            }
        } ?: run {
            innerAttach = null
        }
    }

    /**
     * 获取实际的 midi code, 建议使用 `actualCode` 而不是 `code`
     */
    val actualCode: Int get() = code + alter

    var pitch: Int = 0
        get() {
            return code / 12 - 1
        }

        set(value) {
            code = code % 12 + (value + 1) * 12
            field = value
        }

    val actualPitch: Int
        get() {
            return actualCode / 12 - 1
        }

    val name: String get() = if (alter >= 0) noteNameFromCode(code) else noteNameFromCodeFlat(code)

    val actualName: String get() = if (alter >= 0) noteNameFromCode(actualCode) else noteNameFromCodeFlat(actualCode)

    override fun modifyTargetVelocity(value: Int) {
        velocity = value
    }

    override fun modifyTargetOnVelocity(value: Int) {
        noteOnVelocity = value
    }

    override fun modifyTargetOffVelocity(value: Int) {
        noteOffVelocity = value
    }

    operator fun plusAssign(addPitch: Int) {
//        code += addPitch * 12
        pitch += addPitch
    }

    operator fun minusAssign(addPitch: Int) {
//        code -= addPitch * 12
        pitch -= addPitch
    }

    /**
     * 直接修改 code, 相比 sharp 的方式不会改变 attach, 即不会存储升降号信息
     */
    fun up(times: Int = 1) {
        code = (code + times) % 128
    }

    /**
     * 直接修改 code, 相比 sharp 的方式不会改变 attach, 即不会存储升降号信息
     */
    fun down(times: Int = 1) {
        code -= times
    }

    /**
     * 通过修改 attach 的方式修改 code, 通过 actual 获得真实的 code
     * 可以保留升降号信息
     */
    override fun sharp(times: Int) {
        alter += times
    }

    /**
     * 通过修改 attach 的方式修改 code, 通过 actual 获得真实的 code
     * 可以保留升降号信息
     */
    override fun flap(times: Int) {
        alter -= times
    }

    fun upperNoteName(times: Int = 1): Note {
        for (i in 0 until times)
            code += nextNoteIntervalInMajorScale(code)
        return this
    }

    fun lowerNoteName(times: Int = 1): Note {
        for (i in 0 until times)
            code -= previousNoteIntervalInMajorScale(code)
        return this
    }

    override fun clone(): Note {
        val note = Note(code, duration.clone(), velocity, isNature, alter, attach?.clone())
        note.noteOnVelocity = noteOnVelocity
        note.noteOffVelocity = noteOffVelocity
        return note
    }

    override fun higherOctave(pitch: Int) {
        this += pitch
    }

    override fun lowerOctave(pitch: Int) {
        this -= pitch
    }

    override fun modifyTargetPitch(given: Int) {
        pitch = given
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Note) false else {
            actualCode == other.actualCode &&
            duration == other.duration &&
            velocity == other.velocity &&
            isNature == other.isNature &&
            attach == other.attach
        }
    }

    override fun toString(): String = "[$actualCode=${noteNameFromCode(actualCode)}$actualPitch|$duration|${
        (if (noteOnVelocity != velocity && noteOffVelocity != velocity) "" else "$velocity") +
        (if (noteOnVelocity != velocity) "↓$noteOnVelocity" else "") +
        (if (noteOffVelocity != velocity) "↑$noteOffVelocity" else "")
    }${attach?.let { "" + it } ?: "" }]"

    override fun hashCode(): Int {
        var result = actualCode
        result = 31 * result + duration.hashCode()
        result = 31 * result + velocity
        result = 31 * result + isNature.hashCode()
        result = 31 * result + (attach?.hashCode() ?: 0)
        result = 31 * result + actualCode
        return result
    }
}

/**
 * 音符上的附加信息
 *
 */
open class Attach(var lyric: String? = null) : Cloneable {
    open fun copy(value: NoteAttach) {
        value.lyric?.let { this.lyric = it }
    }

    override fun clone(): Attach {
        return Attach(lyric)
    }
}

class NoteAttach(
    lyric: String? = null,
    var channel: Int? = null,
    var gap: RelativeTicks? = null
) : Attach(lyric) {
    override fun equals(other: Any?): Boolean {
        return if (other !is NoteAttach) false else {
            lyric == other.lyric && channel == other.channel
        }
    }

    override fun copy(value: NoteAttach) {
        super.copy(value)
        value.channel?.let { this.channel = it }
        value.gap?.let { this.gap = it }
    }

    fun clearChannel() {
        channel = null
    }
    fun clearGap() {
        gap = null
    }
    fun clearLyric() {
        lyric = null
    }

    override fun clone(): NoteAttach {
        return NoteAttach(lyric, channel, gap)
    }

    override fun toString(): String = StringBuilder().apply {
        lyric?.let {
            append("<lyric: $it>")
        }

        channel?.let {
            append("<$it>")
        }
        gap?.let {
            append("<$it>")
        }
    }.toString()

    override fun hashCode(): Int {
        var result = channel ?: 0
        result = 31 * result + (gap?.hashCode() ?: 0)
        return result
    }
}
