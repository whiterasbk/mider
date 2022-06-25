package whiter.music.mider.descr

enum class ArpeggioType {
    None, Ascending, Downward
}

class Chord(vararg firstNotes: Note) : InMusicScore {

    var attach: ChordAttach? = null
    var arpeggio: ArpeggioType = ArpeggioType.None

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

        val one = Chord(*cloneNotes.toTypedArray())
        one.attach = attach
        return one
    }

    fun last() = notes.last()

    operator fun plusAssign(note: Note) {
        notes += note
    }

    override fun toString(): String = "Chord: " + notes.joinToString(" ")
}

class ChordAttach(alter: Int = 0, lyric: String? = null) : Attach(alter, lyric)