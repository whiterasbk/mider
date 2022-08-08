package whiter.music.mider.descr

class Rest(override val duration: DurationDescribe = DurationDescribe()) : InMusicScore {
    override fun clone(): Rest {
        return Rest(duration.clone())
    }

    override fun equals(other: Any?): Boolean = if (other !is Rest) false else duration == other.duration

    override fun toString(): String = "[Rest|$duration]"

    override fun hashCode(): Int {
        return duration.hashCode()
    }
}