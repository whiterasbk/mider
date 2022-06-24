package whiter.music.mider.descr

class Rest(override val duration: InMusicScore.DurationDescribe = InMusicScore.DurationDescribe()) : InMusicScore {
    override fun clone(): Rest {
        return Rest(duration.clone())
    }

    override fun toString(): String = "[Rest|$duration]"
}