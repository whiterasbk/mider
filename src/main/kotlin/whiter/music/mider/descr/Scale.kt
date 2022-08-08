package whiter.music.mider.descr

import whiter.music.mider.annotation.Tested

class Scale(vararg inputNotes: Note) : InMusicScore, NoteContainer {

    companion object {
        fun generate(from: Note, to: Note, defaultDuration: DurationDescribe? = null): Scale {
            if (from.actualCode > to.actualCode) throw Exception("from.code has to > to.code")
            val list = mutableListOf<Note>()
            for (i in from.actualCode..to.actualCode) {
                list += Note(i, defaultDuration ?: from.duration, from.velocity)
            }
            return Scale(*list.toTypedArray())
        }
    }

    override val duration: DurationDescribe
        get() {
            val duration = DurationDescribe()
            notes.forEach {
                duration += it.duration
            }

            return duration
        }

    override val notes: MutableList<Note> = inputNotes.toMutableList()

    override fun clone(): Scale {
        val cloneNotes = mutableListOf<Note>()
        notes.forEach {
            cloneNotes += it.clone()
        }

        return Scale(*cloneNotes.toTypedArray())
    }

    @Tested
    infix fun step(number: Int) {
        val first = notes.first()
        val last = notes.last()

        val list = mutableListOf<Note>()
        for (i in first.actualCode..last.actualCode step number) {
            list += Note(i, first.duration, first.velocity)
        }
        notes.clear()
        notes += list
    }

    @Tested
    infix fun under(mode: Array<Int>) {
        var i = notes.first().actualCode
        var loopc = 0
        val list = mutableListOf<Note>()
        while (i <= notes.last().actualCode) {
            list += Note(i, notes.first().duration, notes.first().velocity)
            i += mode[loopc % mode.size]
            loopc++
        }
        notes.clear()
        notes += list
    }

    override fun toString(): String = notes.joinToString("\n")

    override fun equals(other: Any?): Boolean {
        return if (other !is Scale) false else {
            notes == other.notes && duration == other.duration
        }
    }

    override fun hashCode(): Int {
        var result = duration.hashCode()
        result = 31 * result + notes.hashCode()
        return result
    }
}
