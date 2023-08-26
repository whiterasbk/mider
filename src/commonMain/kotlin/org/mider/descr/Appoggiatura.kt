package org.mider.descr

/**
 * 倚音
 * @param second 倚音
 * @param main 主音
 * @param isFront 是否为前倚音
 */
class Appoggiatura(val second: Note, val main: Note, var isFront: Boolean = true) : InMusicScore, CanModifyTargetVelocity, CanModifyTargetDuration, CanModifyTargetPitch {
    override val duration: DurationDescribe = second.duration

    override fun clone(): Appoggiatura {
        return Appoggiatura(second.clone(), main.clone(), isFront)
    }

    override fun getTargetDuration(): DurationDescribe = main.duration

    override fun modifyTargetVelocity(value: Int) {
        main.velocity = value
    }

    override fun modifyTargetOnVelocity(value: Int) {
        main.noteOnVelocity = value
    }

    override fun modifyTargetOffVelocity(value: Int) {
        main.noteOffVelocity = value
    }

    override fun modifyTargetPitch(given: Int) {
        main.pitch = given
    }

    override fun toString(): String = "Appoggiatura: $second $main"

    override fun equals(other: Any?): Boolean {
        return if (other !is Appoggiatura) false else {
            second == other.second && main == other.main && isFront == other.isFront
        }
    }

    override fun hashCode(): Int {
        var result = second.hashCode()
        result = 31 * result + main.hashCode()
        result = 31 * result + isFront.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}