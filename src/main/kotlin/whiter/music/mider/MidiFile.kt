package whiter.music.mider

import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.Channel
import java.nio.channels.Channels
import java.nio.channels.WritableByteChannel

interface HasByteSize {
    fun getOccupiedBytes(): Int
}

interface HexData {
    @Deprecated("use passDataToFileChannel")
    fun getHexDataAsByteBuffer(): ByteBuffer
}

class MidiFile(private val format: MidiFormat = MidiFormat.MIDI_MULTIPLE, private val trackdiv: Int = 960, bufferSize: Int = 100 * 1024, debugOutput: Boolean = false) {
    private val trackChain = mutableListOf<Track>()
    private val buffer = ByteBuffer.allocate(bufferSize)
    var debug = false

    fun append(track: Track): MidiFile {
        trackChain.add(track)
        return this
    }

    inline fun append(block: MidiFile.() -> Unit): MidiFile {
        with(this, block)
        return this
    }

    fun track(block: Track.() -> Unit): Track {
        val t = Track()
        t.append(block)
        append(t)
        return t
    }

    fun channel(channel: WritableByteChannel) {
        val seclen = 6
        val capacity = 4 + 4 + seclen
        channel.write(with(buffer) {
            clear()
            put(HexConst.Mthd)
            put(seclen.as4lByteArray())
            put(format.ordinal.as2lByteArray())
            put(trackChain.size.as2lByteArray())
            put(trackdiv.as2lByteArray())
            flip()
        })

        trackChain.forEach {
            it.writeHead(channel, buffer)
            it.writeMessage(channel, buffer)
        }

//        channel.close()
    }

    fun getFileSize(): Int {
        return trackChain.sumOf { it.headOccupied + it.messageOccupied } + 6 + 4 + 4
    }

    fun inStream(): InputStream {
        val baos = ByteArrayOutputStream(getFileSize())
        val channel = Channels.newChannel(baos)
        channel(channel)
        val array = baos.toByteArray()
        channel.close()
        return ByteArrayInputStream(array)
    }

    fun save(fileName: String) {
        val file = File(fileName)
        val fos = FileOutputStream(file)
        fos.channel.use {
            channel(it)
        }
        fos.close()

        file.setLastModified(System.currentTimeMillis())
    }
}