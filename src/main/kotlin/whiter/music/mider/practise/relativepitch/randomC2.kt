package whiter.music.mider.practise.relativepitch

import whiter.music.mider.dsl.play
import java.util.*

fun main(args: Array<String>) {
//    practise1()
    play {
        A;A;A;
    }
}

fun practise1() {

    println("tips: 输入答案时可以用唱名, 也可以用音名或者音名加数字的是形式, 不区分大小写; 不写音高的时默认为2, 高八度的C必须要写成C3或8")

    val scanner = Scanner(System.`in`)
    print("输入训练次数: ")
    val times = scanner.nextInt()
    val list = mutableListOf<String>()
    for (i in 0 until times) {
        val char = "CDEFGAB".random()
        list += if (char == 'C') listOf("C", "C[3]").random()
        else char.toString()
    }

    val answer = list.map { if (it != "C[3]") "$it[2]" else it }

    var right = 0

    fun String.simpler() = replace("[", "").replace("]", "")

    fun success(it: String) {
        println("${ColorUnix.ANSI_GREEN}答案正确: ${it.simpler()}${ColorUnix.ANSI_RESET}")
        right ++
        play { '2' { +it } }
    }

    fun fail(it: String) {
        println("${ColorUnix.ANSI_RED}答案错误, 正确答案: ${it.simpler()}${ColorUnix.ANSI_RESET}")
        play { '2' { +it } }
    }

    answer.forEachIndexed { index, note ->
        play {
            C[2]..C[3] under majorScale
            repeat(2) { C[2] * 3}
            '2' { +note }
        }

        print("第${ColorUnix.ANSI_BLUE} ${index + 1} ${ColorUnix.ANSI_RESET}道题, 请输入答案: ")
        val input = scanner.next()

        try {
            val real = note.simpler()

            input.toIntOrNull()?.let {
                if (getNoteName(it) == real) {
                    success(note)
                } else {
                    fail(note)
                }
            } ?: run {
                if (input.uppercase() == real) {
                    success(note)
                } else if (input.uppercase().first() == real.first()) {
                    if (real.startsWith("C")) {
                        if (input.length == 1 && real == "C2") {
                            success(note)
                        } else fail(note)
                    } else success(note)
                } else fail(note)
            }

            println()
            Thread.sleep(500)
        } catch (e: Exception) {
            println("请输入正确的格式, 本次跳过, ${e.message}")
        }
    }

    println("练习结束, 总共${ColorUnix.ANSI_BLUE} $times ${ColorUnix.ANSI_RESET}道题, 正确${ColorUnix.ANSI_BLUE} $right ${ColorUnix.ANSI_RESET}道, 正确率${ColorUnix.ANSI_BLUE} ${right.toDouble() / times * 100}%${ColorUnix.ANSI_RESET}")
}

fun getNoteName(int: Int): String {
    return when(int) {
        1 -> "C2"
        2 -> "D2"
        3 -> "E2"
        4 -> "F2"
        5 -> "G2"
        6 -> "A2"
        7 -> "B2"
        8 -> "C3"
        else -> TODO("not yet impl")
    }
}

private object ColorUnix {
    const val ANSI_RESET = "\u001B[0m"

    const val ANSI_BLACK = "\u001B[30m"

    const val ANSI_RED = "\u001B[31m"

    const val ANSI_GREEN = "\u001B[32m"

    const val ANSI_YELLOW = "\u001B[33m"

    const val ANSI_BLUE = "\u001B[34m"

    const val ANSI_PURPLE = "\u001B[35m"

    const val ANSI_CYAN = "\u001B[36m"

    const val ANSI_WHITE = "\u001B[37m"
}