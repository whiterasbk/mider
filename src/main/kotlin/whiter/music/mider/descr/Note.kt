package whiter.music.mider.descr

import whiter.music.mider.nextNoteIntervalInMajorScale
import whiter.music.mider.noteBaseOffset
import whiter.music.mider.noteNameFromCode
import whiter.music.mider.previousNoteIntervalInMajorScale

/**
 * 描述一个音符, 信息更全面
 * @param code 音符代码 0~127
 */
class Note(
    var code: Int,
    override val duration: InMusicScore.DurationDescribe = InMusicScore.DurationDescribe(),
    val velocity: Int = 100,
    var isNature: Boolean = false, // 是否添加了还原符号
    var attach: NoteAttach? = null
) : InMusicScore {

    constructor(name: String, pitch: Int = 4, duration: InMusicScore.DurationDescribe = InMusicScore.DurationDescribe(), velocity: Int = 100)
            : this(noteBaseOffset (name) + (pitch + 1) * 12, duration, velocity)
    constructor(name: Char, pitch: Int = 4, duration: InMusicScore.DurationDescribe = InMusicScore.DurationDescribe(), velocity: Int = 100)
            : this(name.uppercase(), pitch, duration, velocity)

    var pitch: Int = 0
        get() {
            return code / 12 - 1
        } set(value) {
        code = code % 12 + (value + 1) * 12
        field = value
    }

    operator fun plusAssign(addPitch: Int) {
        code += addPitch * 12
    }

    operator fun minusAssign(addPitch: Int) {
        code -= addPitch * 12
    }

    fun sharp(times: Int = 1) {
        code = (code + times) % 128
    }

    fun flap(times: Int = 1) {
        code -= times
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
        return Note(code, duration.clone())
    }

    override fun toString(): String = "[$code=${noteNameFromCode(code)}$pitch|$duration|$velocity]"
}

/**
 * 音符上的附加信息
 * @param alter 正数为升负数为降
 */
data class NoteAttach(val alter: Int = 0)