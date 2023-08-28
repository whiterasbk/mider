
(: define sycth ->

:)

val scope = (: define symbol ->

    class Note (val name: String) {

    }

    O as Rest(.25)
    o as Rest(.125)
    A as Note('A4')
    + as operator with Note +
    ^ as clone operator
    ~ as clone operator
    # as operator with # Note
    : as operator

    fun (n: Note)>+> {
        n.duration *= 2
    }

    fun >#>(n: Note) {
        n.sharp()
    }

    fun (n1: Note)>:>(n2: Note) {
        pop n1
        pop n2
        push Chord(n1, n2)
    }

    fun drop(noteList: List<Note>) {
        >drop(ABC)>
        for (i in noteList) i.code ++
    }

    >: config :>
    Note need octave as provide parameter 1
    Note need duration as provide parameter 2
    Note need velocity as provide parameter 2
:)

scope => { // scope
    octave = 4
    duration = .125
    velocity = 100
    >OooGG^~^~>
}