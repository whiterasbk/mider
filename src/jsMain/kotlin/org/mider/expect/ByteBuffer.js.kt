package org.mider.expect

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

actual class ByteBuffer actual constructor(capacity: Int) {
    private val buffer: Uint8Array = Uint8Array(capacity)
    private var position: Int = 0
    val prototype: Uint8Array get() = buffer

    actual companion object {
        actual fun allocate(occupied: Int): ByteBuffer = ByteBuffer(occupied)
    }

    actual fun put(byte: Byte) {
        buffer[position] = byte
        position ++
    }

    actual fun put(bytes: ByteArray) {
        buffer.set(bytes.toTypedArray(), position)
        position += bytes.size
    }

    actual fun put(buff: ByteBuffer) {
         buffer.set(buff.buffer, position)
         position += buff.position
    }

    actual fun clear() {
        for (e in 0 ..< position) buffer[e] = 0
    }

    actual fun flip() {
        position = 0
    }

    actual fun get(dst: ByteArray, offset: Int, length: Int) {
        var i = 0
        for (e in offset ..< length)
            dst[i++] = buffer[e]
    }

    fun getByteBuffer(dst: ByteBuffer, offset: Int, length: Int): ByteBuffer {
        val ret = ByteBuffer(length)
        for (e in offset ..< length)
            ret.put(buffer[e])
        return ret
    }
}