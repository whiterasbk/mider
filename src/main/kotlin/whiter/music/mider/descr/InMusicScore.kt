package whiter.music.mider.descr

import kotlin.math.abs
import kotlin.math.pow

interface InMusicScore: Cloneable {
    val duration: DurationDescribe
    public override fun clone(): InMusicScore

    class DurationDescribe (
        var bar: Int = 0, // 符杆数, 默认为 0 也就是 四分音符
        var dot: Int = 0, // 附点数
        var default: Double = .25 // 默认为四分音符时值
    ): Cloneable {

        val point: DurationDescribe get() {
            dot ++
            return this
        }

        val halve: DurationDescribe get() {
            bar --
            return this
        }

        val double: DurationDescribe get() {
            bar ++
            return this
        }

        val value: Double get() = default * 2.0.pow(bar) * 1.5.pow(dot)

        public override fun clone(): DurationDescribe {
            return DurationDescribe(bar, dot)
        }

        override fun toString(): String = value.toString()

        operator fun plus(duration: DurationDescribe): DurationDescribe {
            return DurationDescribe(bar + duration.bar, dot + duration.dot, default)
        }
    }



    operator fun timesAssign(times: Int) {
        if (times > 0) {
            for (i in 0 until times)
                duration.double
        } else if (times < 0) {
            for (i in 0 until abs(times))
                duration.halve
        }
    }
}