package org.mider.tonejs.databind

external class Track {
    val channel: Int
    val controlChanges: ControlChanges
    val endOfTrackTicks: Int
    val instrument: Instrument
    val name: String
    val notes: List<Note>
    val pitchBends: List<Any>
}