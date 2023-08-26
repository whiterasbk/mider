package org.mider.expect

expect class ByteBuffer(capacity: Int) {

    companion object {
        fun allocate(occupied: Int): ByteBuffer
    }

    fun put(byte: Byte)
    fun put(bytes: ByteArray)
    fun put(buff: ByteBuffer)
    fun clear()
    fun flip()
    fun get(dst: ByteArray, offset: Int = 0, length: Int)
}