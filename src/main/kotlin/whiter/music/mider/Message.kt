package whiter.music.mider

import java.nio.ByteBuffer

enum class MessageType {
    note_off, note_on, polytouch, control_change, program_change,
    aftertouch, pitchwheel, sysex, quarter_frame, songpos, song_select,
    tune_request, clock, start, `continue`, stop, active_sensing, reset
}

/*
channel	0..15	0
frame_type	0..7	0
frame_value	0..15	0
control	0..127	0
note	0..127	0
program	0..127	0
song	0..127	0
value	0..127	0
velocity	0..127	64
data	(0..127, 0..127, …)	() (empty tuple)
pitch	-8192..8191	0
pos	0..16383	0
time	any integer or float
*/

interface IMessage: HasByteSize, HexData {

    val head: ByteArray
        get() = HexConst.Mtrk

    var deltaTimeArray: ByteArray
}

class Message(
    val event: Event,
    val time: Int = 0,
//    val data: Int,
//    val type: MessageType,
//    val channel: Int = 0,
//    val frame_type: Int = 0,
//    val frame_value: Int = 0,
//    val control: Int = 0,
//    val note: Note = Note.`C-1`,
//    val program: Int = 0,
//    val song: Int = 0,
//    val value: Int = 0,
//    val velocity: Int = 64,
//    val pitch: Int = 0,
//    val pos: Int = 0,
) : IMessage {

    constructor(eventType: EventType, note: Note, time: Int = 0, velocity: Byte = 100, channel: Byte = 0)
            : this(Event(eventType, byteArrayOf(note.id, velocity), channel), time)
    constructor(eventType: EventType, data: ByteArray, time: Int = 0, channel: Byte = 0)
            : this(Event(eventType, data, channel), time)

    override var deltaTimeArray: ByteArray
        get() = time.asvlByteArray()
        set(value) {}

    override fun getOccupiedBytes() = event.getOccupiedBytes() + deltaTimeArray.size

    override fun getHexDataAsByteBuffer(): ByteBuffer {
        val occupied = getOccupiedBytes()
        val buffer = ByteBuffer.allocate(occupied)
        with(buffer) {
            put(event.getHexDataAsByteBuffer())
            put(deltaTimeArray)
            flip()
        }
        return buffer
    }

    override fun toString(): String {
        return "[event: $event time: $time]"
    }
}

class MetaMessage(val metaEvent: MetaEvent, var time: Int = 0, val status: Byte = 0xff.toByte()) : IMessage, HexData {

    constructor(metaEventType: MetaEventType, args: ByteArray = HexConst.emptyData)
            : this(MetaEvent(metaEventType, args))

    override var deltaTimeArray: ByteArray
        get() = time.asvlByteArray()
        set(value) {}

    override fun getHexDataAsByteBuffer(): ByteBuffer {
        val occupied = getOccupiedBytes()
        val buffer = ByteBuffer.allocate(occupied)
        with(buffer) {
            put(status)
            put(metaEvent.getHexDataAsByteBuffer())
            put(deltaTimeArray)
            flip()
        }

        return buffer
    }

    override fun toString(): String {
        return "meta: [event: $metaEvent, status: $status, deltaTime: $time]"
    }

    override fun getOccupiedBytes(): Int {
        return 1 + metaEvent.getOccupiedBytes() + deltaTimeArray.size
    }
}