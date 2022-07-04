package whiter.music.mider.descr

import kotlin.math.abs
import kotlin.math.pow

interface InMusicScore: Cloneable {
    val duration: DurationDescribe
    public override fun clone(): InMusicScore

    class DurationDescribe (
        var bar: Int = 0, // 符杆数, 默认为 0 也就是 四分音符
        var dot: Int = 0, // 附点数
        var default: Double = .25, // 默认为四分音符时值
        var denominator: Double = 1.0 // 默认分母为 1
    ): Cloneable {

        var multiple: Double = 1.0
        val durationList = mutableListOf<DurationDescribe>()
        val baseValue: Double get() = default * 2.0.pow(bar) * 1.5.pow(dot)
        val value: Double get() = calc() + multiple / denominator * baseValue

        private fun calc(): Double {
            return durationList.sumOf { it.value }
        }

        val point: DurationDescribe get() {
            dot ++
            return this
        }

        fun points(times: Int): DurationDescribe {
            for (i in 0 until times) point
            return this
        }

        val halve: DurationDescribe get() {
            bar --
            return this
        }

        fun halves(times: Int): DurationDescribe {
            for (i in 0 until times) halve
            return this
        }

        val double: DurationDescribe get() {
            bar ++
            return this
        }

        fun double(times: Int): DurationDescribe {
            for (i in 0 until times) double
            return this
        }

        public override fun clone(): DurationDescribe {
            return DurationDescribe(bar, dot)
        }

        override fun toString(): String = value.toString()

        operator fun plus(duration: DurationDescribe): DurationDescribe {
            val one = clone()
            one += duration
            return one
        }

        operator fun plusAssign(duration: DurationDescribe) {
            durationList += duration.clone()
        }

        override fun equals(other: Any?): Boolean {
            return if (other !is DurationDescribe) false else {
                value == other.value
            }
        }

        override fun hashCode(): Int {
            var result = bar
            result = 31 * result + dot
            result = 31 * result + default.hashCode()
            result = 31 * result + denominator.hashCode()
            result = 31 * result + durationList.hashCode()
            return result
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
}

interface CanModifyTargetDuration {
    fun getTargetDuration(): InMusicScore.DurationDescribe
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