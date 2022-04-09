package whiter.music.mider

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


fun temp() {
    /*
    with(track) {
        append(Message(Event(whiter.music.mider.EventType.program_change, 0)))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.G4, 480))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.G4, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.E5, 480 * 2))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.E5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.E5, 480))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.E5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.G4, 480))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.G4, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.D5, 480 * 2))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.D5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.D5, 480))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.D5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.G4, 480))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.G4, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.C5, 480 * 2))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.C5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.C5, 480 * 2))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.C5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.C5, 480 * 2))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.C5, 0))
        append(Message(whiter.music.mider.EventType.note_on, whiter.music.mider.Note.D5, 480))
        append(Message(whiter.music.mider.EventType.note_off, whiter.music.mider.Note.D5, 0))

        append(MetaMessage(whiter.music.mider.MetaEventType.META_END_OF_TRACK))
    }
     */
}
