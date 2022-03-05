package whiter.music.mider
import whiter.music.mider.MetaEventType.*
import whiter.music.mider.EventType.*
import whiter.music.mider.Note.*

class Mider {

    var minimsTicks = 960
    var velocity: Byte = 100
    var bpm = 80
    var pitch: Byte = 4
    /**
     * 默认音符时值为四分音符
     */
    var duration = 4
    var format: MidiFormat = MidiFormat.MIDI_MULTIPLE
    private lateinit var midiFile: MidiFile
    private lateinit var main_track: Track
    private lateinit var meta_track: Track

//    private lateinit var current_note: Note

    val instances = listOf(NoteObject(C4))
    val C: NoteObject
        get() {
            genNoteMessage(C4, pitch)
            return instances[0]
        }

    inner class NoteObject(val type: Note) {



        operator fun get(pitch: Byte) {
            // genNoteMessage()
        }
    }

    private fun initMainTrack() : Mider {
        main_track = Track().append {
            message(program_change, byteArrayOf(0))
        }
        return this
    }

    private fun initMetaTrack() : Mider {
        meta_track = Track().append {
            meta(META_TEMPO, bpm(bpm))
            meta(META_END_OF_TRACK)
        }
        return this
    }

    private fun generate() : Mider {
        midiFile = MidiFile(format, minimsTicks)
        midiFile.append(meta_track)
        midiFile.append(main_track)

        return this
    }

    private fun genNoteMessage(current: Note, add_pitch: Byte, time: Float = 1f) {
        val delta_time = (minimsTicks / 2 * time * 4 / duration).toInt()
        main_track.append {
            val note = current + ((add_pitch - pitch) * 12).toByte()
            message(note_on, note, 0, velocity)
            message(note_off, note, delta_time, velocity)
        }
    }

    fun begin(block: Mider.() -> Unit): Mider {
        initMainTrack()
        with(this, block)
        initMetaTrack()
        return this
    }

    fun save(path: String) {
        generate()

        main_track.append {
            meta(META_END_OF_TRACK)
        }

        midiFile.save(path)
    }

    fun C(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(C4, add_pitch, time)
    }

    fun CS(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(CS4, add_pitch, time)
    }

    fun D(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(D4, add_pitch, time)
    }

    fun DS(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(DS4, add_pitch, time)
    }

    fun E(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(E4, add_pitch, time)
    }

    fun F(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(F4, add_pitch, time)
    }

    fun FS(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(FS4, add_pitch, time)
    }

    fun G(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(G4, add_pitch, time)
    }

    fun GS(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(GS4, add_pitch, time)
    }

    fun A(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(A4, add_pitch, time)
    }

    fun AS(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(AS4, add_pitch, time)
    }

    fun B(add_pitch: Byte = 4, time: Float = 1f) {
        genNoteMessage(B4, add_pitch, time)
    }
}