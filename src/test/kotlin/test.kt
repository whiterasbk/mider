import whiter.music.mider.bpm2tempo
import whiter.music.mider.*
import whiter.music.mider.MetaEventType.*
import whiter.music.mider.EventType.*
import whiter.music.mider.Note.*

fun main(args: Array<String>) {

    mid()
}

fun mid() {
    val midi = MidiFile("src/main/resources/test.mid")
    val meta_track = Track()
    meta_track.append(MetaMessage(META_TEMPO, bpm(80)))
    meta_track.append(MetaMessage(META_END_OF_TRACK))
    midi.append(meta_track)

    val track = Track()
    with(track) {
        append(Message(Event(program_change, 0)))
        append(Message(note_on, G4, 480))
        append(Message(note_off, G4, 0))
        append(Message(note_on, E5, 480 * 2))
        append(Message(note_off, E5, 0))
        append(Message(note_on, E5, 480))
        append(Message(note_off, E5, 0))
        append(Message(note_on, G4, 480))
        append(Message(note_off, G4, 0))
        append(Message(note_on, D5, 480 * 2))
        append(Message(note_off, D5, 0))
        append(Message(note_on, D5, 480))
        append(Message(note_off, D5, 0))
        append(Message(note_on, G4, 480))
        append(Message(note_off, G4, 0))
        append(Message(note_on, C5, 480 * 2))
        append(Message(note_off, C5, 0))
        append(Message(note_on, C5, 480 * 2))
        append(Message(note_off, C5, 0))
        append(Message(note_on, C5, 480 * 2))
        append(Message(note_off, C5, 0))
        append(Message(note_on, D5, 480))
        append(Message(note_off, D5, 0))

        append(MetaMessage(META_END_OF_TRACK))
    }

    midi.append(track)

    midi.save()
}

fun bpm(ib: Int): ByteArray {
    val data = bpm2tempo(ib).asByteArray()
    val res = ByteArray(1 + data.size)
    res[0] = data.size.toByte()
    data.forEachIndexed { i, _ ->
        res[i + 1] = data[i]
    }
    return res
}