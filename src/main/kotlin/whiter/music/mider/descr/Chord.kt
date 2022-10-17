package whiter.music.mider.descr

enum class ArpeggioType {
    None, Ascending, Downward
}

class Chord(vararg firstNotes: Note) : InMusicScore, HasFlatAndSharp, HasOctave, CanModifyTargetVelocity, CanModifyTargetDuration, CanModifyTargetPitch, NoteContainer {

    var attach: ChordAttach? = null
    var arpeggio: ArpeggioType = ArpeggioType.None
    var isIndependentDuration: Boolean = false

    init {
        if (firstNotes.isEmpty()) throw Exception("a chord needs notes to buildup")
    }

    override val notes = firstNotes.toMutableList()

    val rootNote get() = notes[0]

    // val secondNote get() = notes[1]
    // val thirdNote get() = notes[2]
    // val forthNote get() = notes[3]
    /**
     * 除根音以外剩下的音符
     */
    val rest: List<Note> get() = notes.subList(1, notes.size)
    override val duration: DurationDescribe = rootNote.duration
    override fun getTargetDuration(): DurationDescribe = last().duration
    override fun modifyTargetVelocity(value: Int) {
        last().velocity = value
    }

    override fun modifyTargetOffVelocity(value: Int) {
        last().noteOffVelocity = value
    }

    override fun modifyTargetOnVelocity(value: Int) {
        last().noteOnVelocity = value
    }

    override fun clone(): Chord {
        val cloneNotes = mutableListOf<Note>()
        notes.forEach {
            cloneNotes += it.clone()
        }

        val one = Chord(*cloneNotes.toTypedArray())
        one.attach = attach
        one.arpeggio = arpeggio
        return one
    }

    override fun sharp(times: Int) = last().sharp(times)

    override fun flap(times: Int) = last().flap(times)

    override fun higherOctave(pitch: Int) = last().higherOctave(pitch)

    override fun lowerOctave(pitch: Int) = last().lowerOctave(pitch)

    override fun modifyTargetPitch(given: Int) {
        last().pitch = given
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Chord) false else {
            notes == other.notes && attach == other.attach && arpeggio == other.arpeggio
        }
    }

    override fun toString(): String = "Chord: " + notes.joinToString(" ")
    override fun hashCode(): Int {
        var result = attach?.hashCode() ?: 0
        result = 31 * result + arpeggio.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}

class ChordAttach(lyric: String? = null) : Attach(lyric)