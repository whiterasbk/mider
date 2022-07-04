package whiter.music.mider.descr

/**
 * 倚音
 * @param main 主音
 * @param second 倚音
 * @param isFront 是否为前倚音
 */
class Appoggiatura(val main: Note, val second: Note, var isFront: Boolean = true) : InMusicScore, CanModifyTargetVelocity, CanModifyTargetDuration, CanModifyTargetPitch {
    override val duration: InMusicScore.DurationDescribe = main.duration

    override fun clone(): Appoggiatura {
        return Appoggiatura(main.clone(), second.clone(), isFront)
    }

    override fun getTargetDuration(): InMusicScore.DurationDescribe = second.duration

    override fun modifyTargetVelocity(value: Int) {
        second.velocity = value
    }

    override fun modifyTargetPitch(given: Int) {
        second.pitch = given
    }

    override fun toString(): String = "Appoggiatura: $main $second"

    override fun equals(other: Any?): Boolean {
        return if (other !is Appoggiatura) false else {
            main == other.main && second == other.second && isFront == other.isFront
        }
    }

    override fun hashCode(): Int {
        var result = main.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + isFront.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}