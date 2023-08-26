package org.mider.expect

actual class ByteBuffer actual constructor(capacity: Int) {

    actual companion object {
        actual fun allocate(occupied: Int): ByteBuffer {
            TODO("Not yet implemented")
        }
    }

    actual fun put(byte: Byte) {
        TODO("Not yet implemented")
    }

    actual fun put(bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    actual fun put(buff: ByteBuffer) {
        TODO("Not yet implemented")
    }

    actual fun clear() {
        TODO("Not yet implemented")
    }

    actual fun flip() {
        TODO("Not yet implemented")
    }

    actual fun get(dst: ByteArray, offset: Int, length: Int) {
        TODO("Not yet implemented")
    }
}