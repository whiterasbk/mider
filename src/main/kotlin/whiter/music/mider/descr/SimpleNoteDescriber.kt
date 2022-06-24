package whiter.music.mider.descr

import whiter.music.mider.noteNameFromCode

data class SimpleNoteDescriber(val name: String, var duration: Double, var pitch: Int = 4, val isRest: Boolean = false) {

    companion object {
        fun fromNote(note: Note): SimpleNoteDescriber {
            return SimpleNoteDescriber(getNoteName(note), note.duration.value, note.pitch)
        }

        fun fromRest(rest: Rest): SimpleNoteDescriber {
            return SimpleNoteDescriber("O", duration = rest.duration.value, isRest = true)
        }

        private fun getNoteName(note: Note): String {
            return if (note.isNature) {
                "!" + noteNameFromCode(note.code).replace("#", "")
            } else noteNameFromCode(note.code)
        }
    }

    override fun toString(): String = if (isRest) "O*$duration" else "$name[$pitch,$duration]"
}