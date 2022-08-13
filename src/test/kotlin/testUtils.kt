import cn.hutool.core.util.XmlUtil
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
import whiter.music.mider.noteBaseOffset
import java.io.File.separator


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

fun String.toPinyin(): String {
    val pyf = HanyuPinyinOutputFormat()
    // 设置大小写
    pyf.caseType = HanyuPinyinCaseType.LOWERCASE
    // 设置声调表示方法
    pyf.toneType = HanyuPinyinToneType.WITH_TONE_NUMBER
    // 设置字母u表示方法
    pyf.vCharType = HanyuPinyinVCharType.WITH_V

    val sb = StringBuilder()
    val regex = Regex("[\\u4E00-\\u9FA5]+")

    for (i in indices) {
        // 判断是否为汉字字符
        if (regex.matches(this[i].toString())) {
            val s = PinyinHelper.toHanyuPinyinStringArray(this[i], pyf)
            if (s != null)
                sb += s[0]

        } else sb += this[i]
    }

    return sb.toString()
}
