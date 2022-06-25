package whiter.music.mider.descr

/**
 * 倚音
 * @param main 主音
 * @param second 倚音
 * @param isFront 是否为前倚音
 */
class Appoggiatura(val main: Note, val second: Note, var isFront: Boolean = true) : InMusicScore {
    override val duration: InMusicScore.DurationDescribe = main.duration

    override fun clone(): Appoggiatura {
        return Appoggiatura(main.clone(), second.clone(), isFront)
    }

    override fun toString(): String = "Appoggiatura: $main $second"
}