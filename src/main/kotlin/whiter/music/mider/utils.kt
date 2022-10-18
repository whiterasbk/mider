package whiter.music.mider

import whiter.music.mider.descr.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.exp
import kotlin.math.ln

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

@OptIn(ExperimentalContracts::class)
@JvmSynthetic
inline fun <reified T : Any> Any?.cast(): T {
    contract {
        returns() implies (this@cast is T)
    }
    return this as T
}


fun ByteArray.showHex() = map {
    if (it >= 0) {
        if (it <= 0xf) "0" + it.toInt().toString(16) else it.toInt().toString(16)
    } else Integer.toHexString(it.toInt()).substring(6, 8)
}


inline fun Array<Int>.asByteArray() = map { it.toByte() }.toByteArray()

@Deprecated("use String.parseToMidiHex instead")
fun String.parseToMidiHexBytes(delimiter: String = trim().let {
    if (it.split(" ").size == 1 && it.length != 2) "" else " "
}) : ByteArray = trim().let {
    val params = it.split(Regex(" "), 2)[1].split(Regex(", *| +"))
    when {
        it.startsWith("o") -> {
        val list = mutableListOf<Byte>()
        val operationOnNote = { opc: Int ->
            val code = params[0].let { name ->
                noteBaseOffset(name.replace(Regex("\\d"), "").let { noteName ->
                    when (noteName.length) {
                        1 -> noteName.last().uppercase()
                        2 -> noteName.first().toString() + noteName.last().uppercase()
                        else -> throw Exception("no such note name")
                    }
                }) + (
                        (name.last().toString().toIntOrNull() ?:
                        if (name.matches(Regex("[#b]?[A-G]"))) 5
                        else if (name.matches(Regex("[#b]?[a-g]"))) 4
                        else throw Exception("name matches failed."))
                                + 1) * 12
            }

            val time = if (1 in params.indices)
                params[1].toInt().asvlByteArray()
            else byteArrayOf(0)

            val velocity = if (2 in params.indices)
                params[2].toInt()
            else 100

            val channel = if (3 in params.indices)
                params[3].toInt()
            else 0

            time.forEach(list::add)
            list += (opc or channel).toByte()
            list += code.toByte()
            list += velocity.toByte()
            // on name time velocity channel
        }
        if (it.startsWith("on")) operationOnNote(0x90)
        else if (it.startsWith("off")) operationOnNote(0x80)
        else throw Exception("no such operation: $it")
        list.toByteArray()
    }

        it.startsWith("i") -> {
            val list = mutableListOf<Byte>()

            val channel = when (it[1]) {
                ' ', '0' -> 0
                '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
                    -> it[1].digitToInt()
                else -> throw Exception("no such channel: ${it[1]}")
            }

            val time = if (1 in params.indices)
                params[1].toInt().asvlByteArray()
            else byteArrayOf(0)

            time.forEach(list::add)
            list += (0xc0 or channel).toByte()
            list += params[0].toByte()

            list.toByteArray()
        }

        else -> (if (delimiter == "") {
            if (it.length % 2 != 0) throw Exception("length of given hex data is expected to be even.")
            else {
                val list = mutableListOf<String>()
                it.forEachIndexed { index, char ->
                    if (index % 2 == 1) list += it[index - 1].toString() + char
                }
                list
            }
        } else it.split(delimiter)).map { byte -> byte.toInt(16).toByte() }.toByteArray()
    }
}


// todo 和 dsl 里的方法合并

/**
 * 获取音符对应的 code 偏移
 */
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

fun minorScaleMapper(name: String): Array<String> {
    return when(name) {
        "C" -> arrayOf("-E", "-A", "-B")
        "D" -> arrayOf("-B")
        "E" -> arrayOf("+F")
        "F" -> arrayOf("-A", "-B", "-D", "-E")
        "G" -> arrayOf("-B", "-E")
        "A" -> arrayOf()
        "B" -> arrayOf("+F", "+C")
        else -> TODO("current mode has not implemented")
    }
}

fun List<InMusicScore>.operationExtendNotes(op: (Note) -> Unit) {
    forEach {
        when (it) {
            is Note -> op(it)
            is NoteContainer -> {
                it.notes.forEach { note ->
                    op(note)
                }
            }
            is Appoggiatura -> {
                op(it.second)
                op(it.main)
            }
        }
    }
}

/**
 * 根据 code 获取音符名称, 只能返回升号
 */
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

/**
 * 根据 code 获取音符名称, 只能返回降号
 */
fun noteNameFromCodeFlat(code: Int): String {
    return when(code % 12) {
        0 -> "C"
        1 -> "bD"
        2 -> "D"
        3 -> "bE"
        4 -> "E"
        5 -> "F"
        6 -> "bG"
        7 -> "G"
        8 -> "bA"
        9 -> "A"
        10 -> "bB"
        11 -> "B"
        else -> throw Exception("no such note code: $code")
    }
}

fun CharSequence.charCount(vararg cmp: Char): Int = filter { char ->
    var result = 0
    cmp.forEach { given ->
        if (char == given) result ++
    }
    result > 0
}.count()

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

fun toMusicXmlKeySignature(ks: String): Pair<Int, String> {

    if (ks.isEmpty()) return 0 to "major"

    val prefix = if (ks.first() in "+-b#") {
        ks.first().toString()
    } else ""

    val name = (if (ks.first() in "+-b#") {
        ks.substring(1, ks.length)
    } else ks)[0].toString()

    val mode = (if (ks.first() in "+-b#") {
        ks.substring(2, ks.length)
    } else ks.substring(1, ks.length))

    val rMode = if (mode == "min" || mode == "minor") "minor" else "major"
    val rPrefix = prefix.replace("-", "b").replace("+", "#")

    val fifths = when (rPrefix + name + rMode) {
        "bCmajor", "bAminor" -> -7
        "bGmajor", "bEminor" -> -6
        "bDmajor", "bBminor" -> -5
        "bAmajor", "Fminor" -> -4
        "bEmajor", "Cminor" -> -3
        "bBmajor", "Gminor" -> -2
        "Fmajor", "Dminor" -> -1
        "Cmajor", "Aminor" -> 0
        "Gmajor", "Eminor" -> 1
        "Dmajor", "Bminor" -> 2
        "Amajor", "#Fminor" -> 3
        "Emajor", "#Cminor" -> 4
        "Bmajor", "#Gminor" -> 5
        "#Fmajor", "#Aminor" -> 6

        else -> 0
    }

    return fifths to rMode
}

// 求给定 index 之后
fun String.nextOnlyInt(index: Int, maxBit: Int): Int {
    var sum = 0
    var count = 0
    for (i in 1 .. maxBit) {
        if (index + i < length) {
            val nextChar = this[index + i]
            if (nextChar in '0'..'9') {
                sum = sum * 10 + (nextChar.code - 48)
                count ++
            } else break
        }
    }

    if (count == 0) throw Exception("there's no integer found after char '${this[index]}', index: $index or maxCount < 1")

    return sum
}

fun String.nextGivenChar(index: Int, char: Char, maxBit: Int): String {
    val sb = StringBuilder()
    var count = 0
    for (i in 1 .. maxBit) {
        if (index + i < length) {
            val nextChar = this[index + i]
            if (nextChar != char) {
                sb.append(nextChar)
                count ++
            } else break
        }
    }

    if (count == 0) throw Exception("there's no char '$char' found after char '${this[index]}', index: $index or maxCount < 1")

    return sb.toString()
}

fun List<Note>.glissandoPoints(): List<Pair<Note, Note>> {
    val list = mutableListOf<Pair<Note, Note>>()
    for (i in 0 until lastIndex) {
        list += this[i] to this[i + 1]
    }
    return list
}

fun String.subStringCount(toBeFound: String): Int {
    var count = 0
    var index = 0
    while (indexOf(toBeFound, index).also { index = it } != -1) {
        index += toBeFound.length
        count ++
    }
    return count
}

fun String.durationSymbolsToMultiple(): Float {
    fun String.div(int: Int) = subStringCount("/$int")
    fun String.mul(int: Int) = subStringCount("x$int")

    trim().let {
        val plus = it.charCount('+')
        val minus = it.charCount('-')
        val dot = it.charCount('.')
        var value = (plus - minus) * ln(2f) + dot * ln(1.5f)

        for (i in 3..9) {
            value += (it.mul(i) - it.div(i)) * ln(i.toFloat())
        }

        return exp(value)
    }
}
