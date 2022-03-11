package whiter.music.mider

import java.io.FileOutputStream
import java.nio.ByteBuffer

interface HasByteSize {
    fun getOccupiedBytes(): Int
}

interface HexData {
    fun getHexDataAsByteBuffer(): ByteBuffer
}

class MidiFile(private val format: MidiFormat = MidiFormat.MIDI_MULTIPLE, private val trackdiv: Int = 960) {
    private val trackChain = mutableListOf<Track>()
    private val buffer = ByteBuffer.allocate(102)
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
        this.append(t)
        return t
    }

    fun data(): ByteBuffer {
        TODO("")
    }

    fun save(fileName: String) {
        val fos = FileOutputStream(fileName)
        val channel = fos.channel
        val seclen = 6
        val capacity = 4 + 4 + seclen
        val header = ByteBuffer.allocate(capacity)
        with(header) {
            put(HexConst.Mthd)
            put(seclen.as4lByteArray())
            put(format.ordinal.as2lByteArray())
            put(trackChain.size.as2lByteArray())
            put(trackdiv.as2lByteArray())
            flip()
        }

        channel.write(header)

        for (i in trackChain) {
            i.outputHead(channel)
            i.outputMessage(channel)
        }

        fos.close()
        channel.close()
    }
}