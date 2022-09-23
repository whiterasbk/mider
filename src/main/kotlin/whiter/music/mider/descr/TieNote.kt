package whiter.music.mider.descr

import whiter.music.mider.noteNameFromCode

class TieNote(vararg noteList: Note) : InMusicScore, CanModifyTargetDuration, NoteContainer {
    override val notes: MutableList<Note> = noteList.toMutableList()
    val main: Note get() = notes.first()

    override val duration: DurationDescribe get() = DurationDescribe(default = notes.sumOf { it.duration.value })

    override fun clone(): TieNote {
        val cloneNotes = mutableListOf<Note>()
        notes.forEach {
            cloneNotes += it.clone()
        }

        return TieNote(*cloneNotes.toTypedArray())
    }

    override fun toString(): String = "[${main.actualCode}=${noteNameFromCode(main.actualCode)}${main.actualPitch}|$duration|${main.velocity}]"

    fun listToString(): String = "TieNote: " + notes.joinToString(" ")

    override fun equals(other: Any?): Boolean {
        return if (other !is TieNote) false else notes == other.notes
    }

    override fun getTargetDuration(): DurationDescribe = notes.last().duration

    override fun hashCode(): Int {
        return notes.hashCode()
    }
}