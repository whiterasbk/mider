package whiter.music.mider.descr

class Chord(vararg firstNotes: Note) : InMusicScore {

    init {
        if (firstNotes.isEmpty()) throw Exception("a chord needs notes to buildup")
    }

    val notes = firstNotes.toMutableList()
    val rootNote get() = notes[0]
    // val secondNote get() = notes[1]
    // val thirdNote get() = notes[2]
    // val forthNote get() = notes[3]
    val rest: List<Note> get() = notes.subList(1, notes.size)
    override val duration: InMusicScore.DurationDescribe = rootNote.duration

    override fun clone(): Chord {
        val cloneNotes = mutableListOf<Note>()
        notes.forEach {
            cloneNotes += it.clone()
        }

        return Chord(*cloneNotes.toTypedArray())
    }

    fun last() = notes.last()

    operator fun plusAssign(note: Note) {
        notes += note
    }

    override fun toString(): String {
        return "Chord: " + notes.joinToString(" ")
    }
}
