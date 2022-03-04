package whiter.music.mider

import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class Track {
    val msgchain = mutableListOf<IMessage>()

    fun seclen(): ByteArray {
        var sum = 0
        for (i in msgchain) {
            sum += i.getOccupiedBytes()
        }
        sum ++ // 00计入
        return sum.as4lByteArray()
    }

    fun append(msg: IMessage): Track {
        msgchain.add(msg)
        return this
    }

    fun outputHead(channels: FileChannel) {
        val occupied = 8 + 1 + 1
        val headerBuffer = ByteBuffer.allocate(occupied)
        with(headerBuffer) {
            put(HexConst.Mtrk)
            put(seclen())
            put(byteArrayOf(0)) // 0似乎是要插入的
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