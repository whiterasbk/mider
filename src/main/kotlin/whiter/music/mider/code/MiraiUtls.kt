package whiter.music.mider.code

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
@JvmSynthetic
public inline fun <reified T : Any> Any?.cast(): T {
    contract {
        returns() implies (this@cast is T)
    }
    return this as T
}

fun noteBaseOffset(note: String): Int {
    return when (note) {
        "C", "#B" -> 0
        "#C", "bD" -> 1
        "D" -> 2
        "#D", "bE" -> 3
        "E", "bF" -> 4
        "F", "#E" -> 5
        "#F", "bG" -> 6
        "G" -> 7
        "#G", "bA" -> 8
        "A" -> 9
        "#A", "bB" -> 10
        "B", "bC" -> 11
        else -> throw Exception("no such note $note")
    }
}


fun nextNoteIntervalInMajorScale(code: Int): Int {
    return when(code % 12) {
        0 -> 2  // C
        1 -> 2  // C#
        2 -> 2  // D
        3 -> 2  // D#
        4 -> 1  // E
        5 -> 2  // F
        6 -> 2  // F#
        7 -> 2  // G
        8 -> 2  // G#
        9 -> 2  // A
        10 -> 2 // A#
        11 -> 1 // B
        else -> 2
    }
}


fun previousNoteIntervalInMajorScale(code: Int): Int {
    return when(code % 12) {
        0 -> 1  // C
        1 -> 2  // C#
        2 -> 2  // D
        3 -> 2  // D#
        4 -> 2  // E
        5 -> 1  // F
        6 -> 2  // F#
        7 -> 2  // G
        8 -> 2  // G#
        9 -> 2  // A
        10 -> 2 // A#
        11 -> 2 // B
        else -> 2
    }
}

fun noteNameFromCode(code: Int): String {
    return when(code % 12) {
        0 -> "C"
        1 -> "#C"
        2 -> "D"
        3 -> "#D"
        4 -> "E"
        5 -> "F"
        6 -> "#F"
        7 -> "G"
        8 -> "#G"
        9 -> "A"
        10 -> "#A"
        11 -> "B"
        else -> throw Exception("no such note code: $code")
    }
}

