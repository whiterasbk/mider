import whiter.music.mider.charCount
import whiter.music.mider.descr.*

fun main(args: Array<String>) {
    val note = Note("F")
    val chord = Chord(Note("C"), Note("D"))

    chord.notes -= note

    println(chord)


    chord + 2
}

operator fun <R: NoteContainer> R.plus(i: Int): R {
    return this
}