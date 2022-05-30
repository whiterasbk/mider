package whiter.music.mider

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun bpm2tempo(bpm: Int) = (60 * 1000000) / bpm

fun Int.as4lByteArray(): ByteArray {
    val bytes = ByteArray(4)
    bytes[0] = (this shr 24 and 0xff).toByte()
    bytes[1] = (this shr 16 and 0xff).toByte()
    bytes[2] = (this shr 8 and 0xff).toByte()
    bytes[3] = this.toByte()
    return bytes
}

fun Int.as3lByteArray(): ByteArray {
    val bytes = ByteArray(3)
    bytes[0] = (this shr 16 and 0xff).toByte()
    bytes[1] = (this shr 8 and 0xff).toByte()
    bytes[2] = this.toByte()
    return bytes
}

fun Int.as2lByteArray(): ByteArray {
    val bytes = ByteArray(2)
    bytes[0] = (this shr 8 and 0xff).toByte()
    bytes[1] = this.toByte()
    return bytes
}

fun Int.as1lByteArray(): ByteArray {
    return byteArrayOf(this.toByte())
}

// variable length byte array
fun Int.asvlByteArray(): ByteArray {
    if (this == 0) return byteArrayOf(0)

    return when (this) {
        in 0..0x7f -> {
            this.as1lByteArray()
        }

        in 0x80..0x3fff -> {
            byteArrayOf(
                (this shr 7 or 0b1000_0000).toByte(),
                (this and 0b0111_1111).toByte())
        }

        in 0x4000..0x1fffff -> {
            byteArrayOf(
                (this shr 14 or 0b1000_0000).toByte(),
                (this shr  7 or 0b1000_0000).toByte(),
                (this and 0b0111_1111).toByte())
        }

        in 0x200000..0xfffffff -> {
            byteArrayOf(
                (this shr 21 or 0b1000_0000).toByte(),
                (this shr 14 or 0b1000_0000).toByte(),
                (this shr  7 or 0b1000_0000).toByte(),
                (this and 0b0111_1111).toByte())
        }

        else -> throw Exception("out of stack: $this, only 4 bytes allow")
    }
}

fun Int.asByteArray(): ByteArray {
    val arr = this.as4lByteArray()
    val u0 = 0.toByte()
    return if (arr[0] == u0) {
        if (arr[1] == u0) {
            if (arr[2] == u0)
                if (arr[3] == u0) byteArrayOf(u0) else byteArrayOf(arr[3])
            else byteArrayOf(arr[2], arr[3])
        } else byteArrayOf(arr[1], arr[2], arr[3])
    } else arr
}

fun bpm(ib: Int): ByteArray {
//    val data = bpm2tempo(ib).asByteArray()
//    val res = ByteArray(1 + data.size)
//    res[0] = data.size.toByte()
//    data.forEachIndexed { i, _ ->
//        res[i + 1] = data[i]
//    }
    return bpm2tempo(ib).asByteArray()
}

operator fun ByteArray.set(range: IntRange, bytes: ByteArray) {
    var count = 0
    for (i in range) {
        this[i] = bytes[count++]
    }
}

class ByteArrayWrap(private vararg val array: Byte) {
    constructor(size: Int): this(*ByteArray(size))

    private var mark: Int = 0
    val size = array.size
    val indices = array.indices
    val lastIndex = array.lastIndex

    fun reset() {
        mark = 0
    }

    fun put(byte: Byte): ByteArrayWrap {
        array[mark++] = byte
        return this
    }

    fun put(bytes: ByteArray): ByteArrayWrap {
        for (i in bytes) {
            put(i)
        }
        return this
    }

    operator fun plusAssign(byte: Byte) {
        put(byte)
    }

    operator fun plusAssign(bytes: ByteArray) {
        put(bytes)
    }

    operator fun not() = array

    operator fun get(index: Int) = array[index]

    operator fun set(index: Int, value: Byte) {
        array[index] = value
    }

    operator fun set(range: IntRange, bytes: ByteArray) {
        array[range] = bytes
    }

    override fun toString(): String = array.toList().toString()
}

@ExperimentalContracts
@JvmSynthetic
public inline fun <reified T : Any> Any?.cast(): T {
    contract {
        returns() implies (this@cast is T)
    }
    return this as T
}


// todo 和dsl里的方法合并

fun noteBaseOffset(note: String): Int {
    return when (note) {
        "C", "#B" -> 0
        "#C", "bD" -> 1
        "D" -> 2
        "#D", "bE" -> 3
        "E", "bF" -> 4
        "F", "#E" -> 5
        "#F", "bG" -> 6
        "G" -> 7
        "#G", "bA" -> 8
        "A" -> 9
        "#A", "bB" -> 10
        "B", "bC" -> 11
        else -> throw Exception("no such note $note")
    }
}

fun noteNameFromCode(code: Int): String {
    return when(code % 12) {
        0 -> "C"
        1 -> "#C"
        2 -> "D"
        3 -> "#D"
        4 -> "E"
        5 -> "F"
        6 -> "#F"
        7 -> "G"
        8 -> "#G"
        9 -> "A"
        10 -> "#A"
        11 -> "B"
        else -> throw Exception("no such note code: $code")
    }
}

fun charCount(str: CharSequence, char: Char): Int {
    return str.filter { it == char }.count()
}

fun deriveInterval(index: Int, scale: Array<Int> = arrayOf(2, 2, 1, 2, 2, 2, 1)): Int {
    var sum = 0
    for (i in 0 until index) {
        sum += scale[i]
    }
    return sum
}

fun nextNoteIntervalInMajorScale(code: Int): Int {
    return when(code % 12) {
        0 -> 2  // C
        1 -> 2  // C#
        2 -> 2  // D
        3 -> 2  // D#
        4 -> 1  // E
        5 -> 2  // F
        6 -> 2  // F#
        7 -> 2  // G
        8 -> 2  // G#
        9 -> 2  // A
        10 -> 2 // A#
        11 -> 1 // B
        else -> 2
    }
}

fun previousNoteIntervalInMajorScale(code: Int): Int {
    return when(code % 12) {
        0 -> 1  // C
        1 -> 2  // C#
        2 -> 2  // D
        3 -> 2  // D#
        4 -> 2  // E
        5 -> 1  // F
        6 -> 2  // F#
        7 -> 2  // G
        8 -> 2  // G#
        9 -> 2  // A
        10 -> 2 // A#
        11 -> 2 // B
        else -> 2
    }
}