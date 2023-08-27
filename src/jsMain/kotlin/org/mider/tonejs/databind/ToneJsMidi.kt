package org.mider.tonejs.databind

@JsModule("@tonejs/midi")
external class ToneJsMidi {
    val header: Header
    val tracks: List<Track>
}