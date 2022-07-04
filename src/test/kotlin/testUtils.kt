import cn.hutool.core.util.XmlUtil
import whiter.music.mider.descr.CanModifyTargetDuration
import whiter.music.mider.noteBaseOffset
import java.lang.StringBuilder

fun String.formatXml(): String = XmlUtil.format(this)

fun String.trimLines(): String = lines().map { it.trim() }.joinToString("")

fun Any?.println() = println(this)

operator fun <E> List<E>.times(times: Int): List<E> {
    val list = mutableListOf<E>()
    for (i in 0 until times) list += this
    return list
}

fun <E> List<E>.removeLastAndReturnSelf(count: Int = 1): List<E> {
    val list = toMutableList()
    for (i in 0 until count) list.removeLast()
    return list
}

inline operator fun StringBuilder.plusAssign(any: Any) {
    append(any)
}

operator fun String.times(times: Int) : String {
    val sb = StringBuilder()
    for (i in 0 until times) sb += this
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
