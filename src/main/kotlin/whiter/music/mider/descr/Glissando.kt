package whiter.music.mider.descr

interface CanBeGlissed : InMusicScore {
    override fun clone(): CanBeGlissed
}

class Glissando(val former: CanBeGlissed, val laster: CanBeGlissed) : InMusicScore {
    var isWave = false
    override val duration: InMusicScore.DurationDescribe = former.duration + laster.duration

    override fun clone(): InMusicScore {
        val glissando = Glissando(former.clone(), laster.clone())
        glissando.isWave = isWave
        return glissando
    }
}