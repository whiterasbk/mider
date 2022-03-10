import whiter.music.mider.*
import whiter.music.mider.MetaEventType.*
import whiter.music.mider.EventType.*
import whiter.music.mider.Note.*

fun main(args: Array<String>) {

    for (i in 0 until 1) println(23)
//    mid()
}

fun mid() {
    val midi = MidiFile()
    midi.append {

        track {
            meta(META_TEMPO, args = bpm(80))
            meta(META_END_OF_TRACK)
        }

        track {
            message(program_change, 0)
            message(note_off, G4, 0)
            message(note_on, G4, 480)
            message(note_off, E5, 0)
            message(note_on, E5, 480)
            message(note_off, E5, 0)
            message(note_on, E5, 480)

            message(note_off, E5, 0)
            message(note_on, E5, 120)

            message(note_off, E5, 0)
            message(note_on, E5, 120)

            message(note_off, E5, 0)
            message(note_on, E5, 120)

            message(note_on, E5, 120)
            message(note_off, E5, 0)




            meta(META_END_OF_TRACK)
        }
    }
    midi.save("src/main/resources/test.mid")

    Mider().begin {
//        G(); E(5, 2f); E(5)
//        G(); D(5, 2f); D(5)
//        G(); C(5, 2f); C(5, 2f); C(5, 2f); D(5)

        duration = 4
        bpm = 80

        G(time = .25f)
        G(time = .25f)
        G(time = .25f)
        G(time = .25f)

        C(time = 1f)
        E(time = 0f)
        G(time = 0f)

        F(time = .5f)
        C(time = .5f)
        F(time = 1f)

        C(); D(); E(); C()
        C(); D(); E(); C()
        E(); F(); G()
        E(); F(); G()

    }.save("src/main/resources/mider.mid")


}