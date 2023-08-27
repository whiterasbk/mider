
import org.khronos.webgl.get
import org.mider.expect.ByteBuffer
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals


class ByteBufferTest {
    @Test
    fun putTest() {
        val size = 256
        val buffer = ByteBuffer(size)
        val random = randomByteArray(size)
        for (i in random) buffer.put(i)

        for (i in random.indices) {
            assertEquals(random[i].toUByte().toString(), buffer.prototype[i].toUByte().toString())
        }
    }

    @Test
    fun putArrayTest() {
        val size = 256
        val array = randomByteArray(size)
        val buffer = ByteBuffer(size)
        buffer.put(array)

        assertEquals(
            buffer.getByteList(size),
            array.asList()
        )
    }

    @Test
    fun putBufferTest() {
        val size = 256
        val buffer1 = randomBuffer(size)

        val buffer2 = ByteBuffer(size)
        buffer2.put(buffer1)

        assertEquals(
            buffer1.getByteList(size),
            buffer2.getByteList(size)
        )
    }

    @Test
    fun putUnitTest() {
        val buffer = ByteBuffer(5)
        buffer.put(19)
        buffer.put(byteArrayOf(19, 8))
        buffer.put(ByteBuffer(1).apply { put(10) })

        assertEquals(listOf<Byte>(19, 19, 8, 10, 0), buffer.getByteArray(5).toList())
    }

    @Test
    fun flipTest() {
        val buffer = ByteBuffer(5)
        buffer.put(19)
        buffer.put(byteArrayOf(19, 8))
        buffer.put(ByteBuffer(2).apply {
            put(10)
            put(11)
        })

        buffer.flip()
        buffer.put(114)
        buffer.put(5)
        buffer.put(14)
        buffer.put(7)

        assertEquals(listOf<Byte>(114, 5, 14, 7, 11),  buffer.getByteArray(5).asList())
    }

    @Test
    fun clearTest() {
        val size = 256
        val buffer = randomBuffer(size)
        buffer.clear()
        assertEquals(ByteArray(size).asList(), buffer.getByteList(size))
    }

    @Test
    fun getTest() {
        val size = 256
        val a = randomByteArray(size)
        val buffer = ByteBuffer(size)
        buffer.put(a)
        val b = ByteArray(size)
        buffer.get(b, 0, size)

        assertEquals(a.asList(), b.asList())
    }

    private fun randomBuffer(size: Int) = ByteBuffer(size).apply {
        put(randomByteArray(size))
    }

    private fun randomByteArray(size: Int) = Random.nextBytes(ByteArray(size))

    private fun ByteBuffer.getByteArray(size: Int) = ByteArray(size).apply {
        get(this, 0, size)
    }

    private fun ByteBuffer.getByteList(size: Int) = getByteArray(size).asList()
}