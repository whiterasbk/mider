package org.mider.descr
import org.mider.impl.Cloneable
import kotlin.math.abs

interface InMusicScore: Cloneable {
    val duration: DurationDescribe
    override fun clone(): InMusicScore

    operator fun timesAssign(times: Int) {
        if (times > 0) {
            for (i in 0..< times)
                duration.double
        } else if (times < 0) {
            for (i in 0..< abs(times))
                duration.halve
        }
    }
}

interface HasFlatAndSharp {
    fun sharp(times: Int = 1)
    fun flap(times: Int = 1)
}

interface HasOctave {
    fun higherOctave(pitch: Int = 1)
    fun lowerOctave(pitch: Int = 1)
}

interface CanModifyTargetVelocity {
    fun modifyTargetVelocity(value: Int)

    fun modifyTargetOnVelocity(value: Int) {
        modifyTargetVelocity(value)
    }

    fun modifyTargetOffVelocity(value: Int) {
        modifyTargetVelocity(value)
    }
}

interface CanModifyTargetDuration {
    fun getTargetDuration(): DurationDescribe
}

interface CanModifyTargetPitch {
    fun modifyTargetPitch(given: Int)
}

interface NoteContainer {

    val notes: MutableList<Note>

    operator fun plusAssign(note: Note) {
        notes += note
    }

    operator fun minusAssign(note: Note) {
        notes -= note
    }

    fun last() = notes.last()
}

/**
 * 标记一个在乐谱中不发出声音的
 */
interface Mute