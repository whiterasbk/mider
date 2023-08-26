package org.mider.expect

actual class ByteBuffer actual constructor(capacity: Int) {

    private val jvmBuffer: java.nio.ByteBuffer

    init {
        jvmBuffer = java.nio.ByteBuffer.allocate(capacity)
    }

    actual companion object {
        actual fun allocate(occupied: Int): ByteBuffer {
            return ByteBuffer(occupied)
        }
    }

    actual fun put(byte: Byte) {
        jvmBuffer.put(byte)
    }

    actual fun put(bytes: ByteArray) {
        jvmBuffer.put(bytes)
    }

    actual fun put(buff: ByteBuffer) {
        jvmBuffer.put(buff.jvmBuffer)
    }

    actual fun clear() {
        jvmBuffer.clear()
    }

    actual fun flip() {
        jvmBuffer.flip()
    }

    actual fun get(dst: ByteArray, offset: Int, length: Int) {
        jvmBuffer.get(dst, offset, length)
    }
}