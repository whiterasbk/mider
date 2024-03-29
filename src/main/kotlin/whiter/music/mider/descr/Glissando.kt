package whiter.music.mider.descr

import whiter.music.mider.cast
//
//interface CanBeGlissed : InMusicScore, HasFlatAndSharp, CanModifyTargetVelocity, HasOctave {
//
//    override fun clone(): CanBeGlissed
//}

/**
 * 滑音
 */
class Glissando(private vararg val args: Note) : InMusicScore, HasFlatAndSharp, CanModifyTargetVelocity, HasOctave, CanModifyTargetDuration, CanModifyTargetPitch, NoteContainer {
    var isWave = false
    var isContainBlack = false
    override val notes = args.toMutableList()

    override val duration: DurationDescribe
        get() {
            val duration = DurationDescribe()
            notes.forEach {
                duration += it.duration
            }

            return duration
        }

    override fun getTargetDuration(): DurationDescribe = notes.last().duration

    override fun modifyTargetPitch(given: Int) {
        notes.last().cast<Note>().pitch = given
    }

    override fun modifyTargetVelocity(value: Int) {
        notes.last().cast<Note>().velocity = value
    }

    override fun modifyTargetOnVelocity(value: Int) {
        notes.last().cast<Note>().noteOnVelocity = value
    }

    override fun modifyTargetOffVelocity(value: Int) {
        notes.last().cast<Note>().noteOffVelocity = value
    }

    override fun clone(): Glissando {
        val cloneNotes = mutableListOf<Note>()
        notes.forEach {
            cloneNotes += it.clone()
        }

        val one = Glissando(*cloneNotes.toTypedArray())
        one.isWave = isWave
        one.isContainBlack = isContainBlack
        return one
    }

    override fun sharp(times: Int) = notes.last().sharp(times)

    override fun flap(times: Int) = notes.last().flap(times)

    override fun higherOctave(pitch: Int) = notes.last().cast<HasOctave>().higherOctave(pitch)

    override fun lowerOctave(pitch: Int) = notes.last().cast<HasOctave>().lowerOctave(pitch)

    override fun toString(): String = "Glissando: ${notes.joinToString(" ")}"

    override fun equals(other: Any?): Boolean {
        return if (other !is Glissando) false else {
            notes == other.notes && isWave == other.isWave && duration == other.duration
        }
    }

    override fun hashCode(): Int {
        var result = args.contentHashCode()
        result = 31 * result + isWave.hashCode()
        result = 31 * result + notes.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}