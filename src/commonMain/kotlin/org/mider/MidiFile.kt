package org.mider

import org.mider.expect.ByteBuffer

interface HasByteSize {
    fun getOccupiedBytes(): Int
}

interface HexData {
    @Deprecated("use passDataToFileChannel")
    fun getHexDataAsByteBuffer(): ByteBuffer
}

class MidiFile(
    private val format: MidiFormat = MidiFormat.MIDI_MULTIPLE,
    private val trackdiv: Int = 960,
    bufferSize: Int = 100 * 1024,
    debugOutput: Boolean = false
) {
    private val trackChain = mutableListOf<Track>()
    private val buffer = ByteBuffer.allocate(bufferSize)
    private var debug = false

    fun append(track: Track): MidiFile = apply { trackChain.add(track) }

    inline fun append(block: MidiFile.() -> Unit): MidiFile = apply(block)

    fun track(block: Track.() -> Unit): Track = Track().apply {
        this.append(block)
        append(this)
    }

    fun doFinal(): ByteBuffer {
        val seclen = 6
        val capacity = 4 + 4 + seclen

        buffer.apply {
            clear()
            put(HexConst.Mthd)
            put(seclen.as4lByteArray())
            put(format.ordinal.as2lByteArray())
            put(trackChain.size.as2lByteArray())
            put(trackdiv.as2lByteArray())
        }

        trackChain.forEach {
            it.writeHead(buffer)
            it.writeMessage(buffer)
        }

        buffer.flip()

        return buffer
    }

    fun getFileSize(): Int {
        return trackChain.sumOf { it.headOccupied + it.messageOccupied } + 6 + 4 + 4
    }
}