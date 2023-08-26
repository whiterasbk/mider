package org.mider

object HexConst {
    val Mthd = byteArrayOf(77, 84, 104, 100)
    val Mtrk = byteArrayOf(77, 84, 114, 107)
    val emptyData = ByteArray(0)
}

enum class MidiFormat(id: Byte) {
    MIDI_SINGLE (0), MIDI_MULTIPLE (1), MIDI_PATTERN (2)
}
