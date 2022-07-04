package whiter.music.mider

import whiter.music.mider.descr.*

fun List<InMusicScore>.convert2MidiEvents(wholeTicks: Int, channel: Int) {
    val msgs = mutableListOf<Message>()

    forEach {
        when (it) {
            is Note -> {
                msgs += noteOnMessage(it.actualCode, 0, it.velocity, channel)
                msgs += noteOffMessage(it.actualCode, it.duration.value * wholeTicks, it.velocity, channel)
            }

            is Chord -> {

            }

            is Rest -> {

            }

            is Appoggiatura -> {

            }

            is Glissando -> {

            }
        }
    }
}

private fun noteOnMessage(code: Int, duration: Number, velocity: Int, channel: Int = 0): Message {
    return Message(EventType.note_on, byteArrayOf(code.toByte(), velocity.toByte()), time = duration.toInt(), channel = channel.toByte())
}

private fun noteOffMessage(code: Int, duration: Number, velocity: Int, channel: Int = 0): Message {
    return Message(EventType.note_off, byteArrayOf(code.toByte(), velocity.toByte()), time = duration.toInt(), channel = channel.toByte())
}