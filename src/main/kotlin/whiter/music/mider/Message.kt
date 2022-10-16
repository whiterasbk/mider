package whiter.music.mider

import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel


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

//    val pos: Int = 0,


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

interface IMessage: HasByteSize, HexData {
    val deltaTimeArray: ByteArray
    fun passDataToChannel(channel: WritableByteChannel, buffer: ByteBuffer)
}

class HexMessage(private val data: ByteArray) : IMessage {



    override val deltaTimeArray: ByteArray get() {
        throw Exception("deltaTimeArray is part of data in HexMessage")
    }

    override fun passDataToChannel(channel: WritableByteChannel, buffer: ByteBuffer) {
        channel.write(with(buffer) {
            clear()
            put(data)
            flip()
        })
    }

    override fun getOccupiedBytes(): Int = data.size

    @Deprecated("use passDataToFileChannel")
    override fun getHexDataAsByteBuffer(): ByteBuffer {
        throw Exception("use passDataToFileChannel")
    }

}

class Message(val event: Event, val time: Int = 0) : IMessage {
    constructor(eventType: EventType, note: MidiNote, time: Int = 0, velocity: Byte = 100, channel: Byte = 0)
            : this(Event(eventType, byteArrayOf(note.id, velocity), channel), time)
    constructor(eventType: EventType, time: Int = 0, vararg data: Byte, channel: Byte = 0)
            : this(Event(eventType, data, channel), time)
    constructor(eventType: EventType, note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0)
            : this(Event(eventType, byteArrayOf(note, velocity), channel), time)
    constructor(eventType: EventType, data: ByteArray, time: Int = 0, channel: Byte = 0)
            : this(Event(eventType, data, channel), time)
    constructor(eventType: EventType, time: Int = 0, vararg data: Byte)
            : this(Event(eventType, args = data, 0), time)
    constructor(eventType: EventType, vararg data: Byte)
            : this(Event(eventType, args = data, 0), 0)

    override val deltaTimeArray: ByteArray get() = time.asvlByteArray()

    override fun getOccupiedBytes() = event.getOccupiedBytes() + deltaTimeArray.size

    @Deprecated("use passDataToFileChannel")
    override fun getHexDataAsByteBuffer(): ByteBuffer {
        val occupied = getOccupiedBytes()
        val buffer = ByteBuffer.allocate(occupied)
        with(buffer) {
            put(deltaTimeArray)
            put(event.getHexDataAsByteBuffer())
            flip()
        }
        return buffer
    }

    override fun passDataToChannel(channel: WritableByteChannel, buffer: ByteBuffer) {
        channel.write(with(buffer) {
            clear()
            put(deltaTimeArray)
            put(event.generateData())
            flip()
        })
    }

    override fun toString(): String {
        return "[event: $event time: $time]"
    }
}

class MetaMessage(val metaEvent: MetaEvent, var time: Int = 0, val status: Byte = 0xff.toByte()) : IMessage, HexData {

//    constructor(metaEventType: MetaEventType, args: ByteArray = HexConst.emptyData)
//            : this(MetaEvent(metaEventType, args))
    constructor(metaEventType: MetaEventType, vararg args: Byte = HexConst.emptyData)
            : this(MetaEvent(metaEventType, args))

    override val deltaTimeArray: ByteArray get() = time.asvlByteArray()

    override fun getHexDataAsByteBuffer(): ByteBuffer {
        val occupied = getOccupiedBytes()
        val buffer = ByteBuffer.allocate(occupied)
        with(buffer) {
            put(deltaTimeArray)
            put(status)
            put(metaEvent.getHexDataAsByteBuffer())
            flip()
        }

        return buffer
    }

    override fun passDataToChannel(channel: WritableByteChannel, buffer: ByteBuffer) {
        channel.write(with(buffer) {
            clear()
            put(deltaTimeArray)
            put(status)
            put(metaEvent.generateData())
            flip()
        })
    }

    override fun toString(): String {
        return "meta: [event: $metaEvent, status: $status, deltaTime: $time]"
    }

    override fun getOccupiedBytes(): Int {
        return 1 + metaEvent.getOccupiedBytes() + deltaTimeArray.size
    }
}