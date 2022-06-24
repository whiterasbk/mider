import cn.hutool.core.util.XmlUtil

fun String.formatXml(): String = XmlUtil.format(this)

fun String.trimLines(): String = lines().map { it.trim() }.joinToString("")

fun Any?.println() = println(this)