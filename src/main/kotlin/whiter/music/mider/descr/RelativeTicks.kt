package whiter.music.mider.descr

import kotlin.properties.Delegates

class RelativeTicks {

    private var relativeDuration by Delegates.notNull<Double>()
    private var ticks by Delegates.notNull<Long>()
    private val pure: Boolean

    constructor(ticks: Long) {
        this.ticks = ticks
        pure = true
    }

    constructor(relative: Double) {
        this.relativeDuration = relative
        pure = false
    }

    fun calcTicks(wholeTicks: Long): Long {
        return if (pure) ticks else (wholeTicks * relativeDuration).toLong()
    }

    override fun toString(): String = if (pure) "ticks: $ticks" else "relative: $relativeDuration"
}