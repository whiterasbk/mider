package org.mider.test

import org.mider.noteBaseOffset
import kotlin.test.assertEquals

val ByteArray.listForm: List<Byte> get() = toList()

fun assertByteEquals(expect: ByteArray, actual: ByteArray) {
    assertEquals(expect.listForm, actual.listForm)
}

fun assertArrayEquals(expect: Array<Any>, actual: Array<Any>) {
    assertEquals(expect.toList(), actual.toList())
}

operator fun <E> List<E>.times(times: Int): List<E> {
    val list = mutableListOf<E>()
    for (i in 0..< times) list += this
    return list
}

fun <E> List<E>.removeLastAndReturnSelf(count: Int = 1): List<E> {
    val list = toMutableList()
    for (i in 0..< count) list.removeLast()
    return list
}

operator fun StringBuilder.plusAssign(any: Any) {
    append(any)
}

fun String.trimLines(): String = lines().map { it.trim() }.joinToString("")

operator fun String.times(times: Int) : String {
    val sb = StringBuilder()
    for (i in 0..< times) sb += this
    return sb.toString()
}

fun generateNoteString(name: String, duration: Double = .25, pitch: Int = 4, velocity: Int = 100): String {
    val rp = when (name.last()) {
        in '0'..'9' -> name.last().code - 48
        else -> pitch
    }

    val noteName = name.replace(Regex("\\d+"), "")
    val code = noteBaseOffset(noteName) + (rp + 1) * 12
    return "[$code=${noteName}$rp|$duration|$velocity]"
}