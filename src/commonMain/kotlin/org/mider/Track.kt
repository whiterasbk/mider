package org.mider

import org.mider.expect.ByteBuffer
import kotlin.jvm.JvmName

//import java.nio.ByteBuffer
//import java.nio.channels.WritableByteChannel

class Track {
    val msgchain = mutableListOf<IMessage>()

    val seclen: ByteArray get() = msgchain.sumOf { it.getOccupiedBytes() }.as4lByteArray()

    fun append(msg: IMessage): Track {
        // todo debug
//        println(msg)
        msgchain.add(msg)
        return this
    }

    fun insertPenultimate(msg: IMessage) {
//        println(msg)
        msgchain.add(msgchain.lastIndex, msg)
    }

    inline fun append(block: Track.() -> Unit): Track {
        with(this, block)
        return this
    }

    fun meta(metaEvent: MetaEvent, time: Int = 0, status: Byte = 0xff.toByte()) {
        append(MetaMessage(metaEvent, time, status))
    }

//    fun meta(metaEventType: MetaEventType, args: ByteArray = HexConst.emptyData) {
//        append(MetaMessage(metaEventType, args))
//    }

    fun meta(metaEventType: MetaEventType, vararg args: Byte = HexConst.emptyData) {
        append(MetaMessage(metaEventType, args = args))
    }

    fun end() {
        meta(MetaEventType.META_END_OF_TRACK)
    }

    fun tempo(bpm: Int) {
        meta(MetaEventType.META_TEMPO, *bpm(bpm))
    }

//    fun message(eventType: EventType, vararg data: Byte) {
//        append(Message(eventType, data = data))
//    }
//
//    fun message(eventType: EventType, time: Int, vararg data: Byte) {
//        append(Message(eventType, time, data = data))
//    }
//
//    fun message(eventType: EventType, time: Int = 0, vararg data: Byte, channel: Byte = 0) {
//        append(Message(eventType, time, data = data, channel))
//    }

//    fun message(eventType: EventType, note: Note, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
//        append(Message(eventType, note, time, velocity, channel))
//    }

//    fun message(eventType: EventType, note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
//        append(Message(eventType, note, time, velocity, channel))
//    }

//    fun message(eventType: EventType, data: ByteArray, time: Int = 0, channel: Byte = 0) {
//        append(Message(eventType, data, time, channel))
//    }

    fun message(event: Event, time: Int = 0) {
        append(Message(event, time))
    }

    fun message(msg: IMessage) {
        append(msg)
    }

    fun messagePenultimate(event: Event, time: Int = 0) {
        insertPenultimate(Message(event, time))
    }

    fun message(event: EventType, time: Int = 0, channel: Byte = 0, vararg data: Byte) {
        message(Event(event, args = data, channel), time)
    }

    fun messaged(eventType: EventType, vararg data: Byte) {
        message(Event(eventType, args = data, 0), 0)
    }

    fun changeProgram(program: Byte) {
        message(Event(EventType.program_change, byteArrayOf(program), 0), 0)
    }

    fun noteOn(note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        message(Event(EventType.note_on, byteArrayOf(note, velocity), channel), time)
    }

    fun noteOff(note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        message(Event(EventType.note_off, byteArrayOf(note, velocity), channel), time)
    }

    fun noteOn(note: MidiNote, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        noteOn(note.id, time, velocity, channel)
    }

    fun noteOff(note: MidiNote, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        noteOff(note.id, time, velocity, channel)
    }

    fun note(note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        noteOn(note, time, velocity, channel)
    }

    fun note(note: MidiNote, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        note(note.id, time, velocity, channel)
    }

    fun noteOnPenultimate(note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        messagePenultimate(Event(EventType.note_on, byteArrayOf(note, velocity), channel), time)
    }

    fun noteOffPenultimate(note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        messagePenultimate(Event(EventType.note_off, byteArrayOf(note, velocity), channel), time)
    }

    fun noteOnPenultimate(note: MidiNote, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        noteOnPenultimate(note.id, time, velocity, channel)
    }

    fun noteOffPenultimate(note: MidiNote, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        noteOffPenultimate(note.id, time, velocity, channel)
    }

    @JvmName("hexArray")
    fun hex(bytes: ByteArray) {
        message(HexMessage(bytes))
    }

    @JvmName("hexVarargByte")
    fun hex(vararg bytes: Byte) = hex(bytes)

    fun hex(hex: String, delimiter: String = " ") {
        hex(hex.let { hex ->
            if (delimiter == "") {
                val list = mutableListOf<String>()
                hex.forEachIndexed { index, c ->
                    if (index % 2 == 1) list += hex[index - 1] + c.toString()
                }
                list
            } else hex.trim().split(delimiter)
        }.map {
            it.toInt(16).toByte()
        }.toByteArray())
    }

//
//    fun message(
//        eventType: EventType,
//        note: Note = Note.C4,
//        time: Int = 0,
//        channel: Byte = 0,
//        velocity: Byte = 100,
//    ) {
//        message(eventType, note.id, time, channel, velocity)
//    }
//
//    fun message(
//        eventType: EventType,
//        note: Byte = 0,
//        time: Int = 0,
//        channel: Byte = 0,
//        velocity: Byte = 100,
//        instrument: Byte = 0
//    ) {
//        when (eventType) {
//            EventType.note_on, EventType.note_off -> {
//                message(Event(eventType, byteArrayOf(note, velocity), channel), time)
//            }
//
//            EventType.program_change -> {
//                message(Event(eventType, byteArrayOf(instrument), channel), time)
//            }
//        }
//    }

    val headOccupied: Int = 8

    fun writeHead(buffer: ByteBuffer) {
        buffer.apply {
            // clear()
            put(HexConst.Mtrk)
            put(seclen)
            // flip()
        }
    }

    val messageOccupied: Int get() = msgchain.sumOf { it.getOccupiedBytes() }

    fun writeMessage(buffer: ByteBuffer) {
        msgchain.forEach {
            it.writeMessageContent(buffer)
//            it.passDataToChannel(channels, buffer)
        }
    }
}
