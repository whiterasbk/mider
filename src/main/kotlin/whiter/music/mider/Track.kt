package whiter.music.mider

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Track {
    val msgchain = mutableListOf<IMessage>()

    val seclen: ByteArray
        get() {
            var sum = 0
            for (i in msgchain) {
                sum += i.getOccupiedBytes()
            }
            return sum.as4lByteArray()
        }

    fun append(msg: IMessage): Track {
        println(msg)
        msgchain.add(msg)
        return this
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

    fun message(event: Event, time: Int = 0) {
        append(Message(event, time))
    }

    fun message(eventType: EventType, time: Int = 0, vararg data: Byte, channel: Byte = 0) {
        append(Message(eventType, time, data = data, channel))
    }

    fun message(eventType: EventType, note: Note, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        append(Message(eventType, note, time, velocity, channel))
    }

    fun message(eventType: EventType, note: Byte, time: Int = 0, velocity: Byte = 100, channel: Byte = 0) {
        append(Message(eventType, note, time, velocity, channel))
    }

    fun message(eventType: EventType, data: ByteArray, time: Int = 0, channel: Byte = 0) {
        append(Message(eventType, data, time, channel))
    }

    fun outputHead(channels: FileChannel) {
        val occupied = 8
        val headerBuffer = ByteBuffer.allocate(occupied)
        with(headerBuffer) {
            put(HexConst.Mtrk)
            put(seclen)
            flip()
        }
        channels.write(headerBuffer)
    }

    fun outputMessage(channels: FileChannel) {
        for (msg in msgchain) {
            channels.write(msg.getHexDataAsByteBuffer())
        }
    }
}