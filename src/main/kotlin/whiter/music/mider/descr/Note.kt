package whiter.music.mider.descr

import whiter.music.mider.*

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
    var velocity: Int = 100,
    var isNature: Boolean = false, // 是否添加了还原符号
    var alter: Int = 0,
    var attach: NoteAttach? = null
) : InMusicScore, HasFlatAndSharp, HasOctave, CanModifyTargetVelocity, CanModifyTargetPitch {

    constructor(name: String, pitch: Int = 4, duration: DurationDescribe = DurationDescribe(), velocity: Int = 100)
            : this(noteBaseOffset (name) + (pitch + 1) * 12, duration, velocity)
    constructor(name: Char, pitch: Int = 4, duration: DurationDescribe = DurationDescribe(), velocity: Int = 100)
            : this(name.uppercase(), pitch, duration, velocity)

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
        return Note(code, duration.clone(), velocity, isNature, alter, attach)
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

    override fun toString(): String = "[$actualCode=${noteNameFromCode(actualCode)}$actualPitch|$duration|$velocity]"

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
open class Attach(var lyric: String? = null)

class NoteAttach(lyric: String? = null) : Attach(lyric) {
    override fun equals(other: Any?): Boolean {
        return if (other !is NoteAttach) false else {
            lyric == other.lyric
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}