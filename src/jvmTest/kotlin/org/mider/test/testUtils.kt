package org.mider.test

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType


//fun String.trimLines(): String = lines().map { it.trim() }.joinToString("")
//
//fun Any?.println() = println(this)

//operator fun <E> List<E>.times(times: Int): List<E> {
//    val list = mutableListOf<E>()
//    for (i in 0..< times) list += this
//    return list
//}
//
//fun <E> List<E>.removeLastAndReturnSelf(count: Int = 1): List<E> {
//    val list = toMutableList()
//    for (i in 0..< count) list.removeLast()
//    return list
//}





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
