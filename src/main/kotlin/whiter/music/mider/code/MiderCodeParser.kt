package whiter.music.mider.code

import whiter.music.mider.*
import whiter.music.mider.descr.*
import whiter.music.mider.descr.Note
import whiter.music.mider.xml.Node
import java.lang.StringBuilder
import java.util.*
import java.io.File
import java.net.URL
import kotlin.contracts.ExperimentalContracts

fun toMiderStanderNoteString(list: List<InMusicScore>): String {
    val result = mutableListOf<SimpleNoteDescriber>()

    list.forEach {
        when (it) {
            is Note -> {
                result += SimpleNoteDescriber.fromNote(it)
            }

            is Chord -> {
                result += SimpleNoteDescriber.fromNote(it.rootNote)

                it.rest.forEach { restNote ->
                    val insert = SimpleNoteDescriber.fromNote(restNote)
                    insert.duration =.0
                    result += insert
                }
            }

            is Rest -> {
                if (result.isEmpty()) {
                    result += SimpleNoteDescriber.fromRest(it)
                } else {
                    val rootNoteIndex = result.indexOfLast { it.duration != .0 }
                    if (rootNoteIndex != -1) {
                        result[rootNoteIndex].duration += it.duration.value
                    }
                }
            }
        }
    }

    return result.joinToString(" ")
}

fun macro(seq: String, config: MacroConfiguration = MacroConfiguration()): String {
    if ('(' !in seq || ')' !in seq) return seq
//    val innerScope = mutableMapOf<String, String>()
    val outerScope = config.outerScope
    val macroScope = config.macroScope
//    val replacePattern = Regex("replace\\s*:\\s*[^>]+")
//    val replaceWith = mutableListOf<MutableList<String>>()

    val innerScopeExecute = { str: String ->
        if (MacroConfiguration.getVariableValuePattern.matches(str)) {
            val symbol = MacroConfiguration.getVariableValuePattern.matchEntire(str)!!.groupValues[1]
            if (symbol !in outerScope) {
                // todo 解决 innerScopeExecute 先于 innerScopeExecute 执行的问题
                config.logger.error(Exception("undefined symbol: $symbol"))
                str
            } else outerScope[symbol]!!
        } else {
            config.logger.error(Exception("unsupported operation in inner: $str"))
            str
        }
    }

    val outerScopeExecute = { str: String ->
        if (MacroConfiguration.definePattern.matches(str)) {
            val symbol = MacroConfiguration.definePattern.matchEntire(str)!!.groupValues[1]
            outerScope[symbol] = str.replace(Regex("def\\s+$symbol\\s*="), "")
            ""
        } else if (MacroConfiguration.executePattern.matches(str)) {
            val symbol = MacroConfiguration.executePattern.matchEntire(str)!!.groupValues[1]
            outerScope[symbol] = str.replace(Regex("def\\s+$symbol\\s*:"), "")
            outerScope[symbol]
        } else if (MacroConfiguration.getVariableValuePattern.matches(str)) {
            val symbol = MacroConfiguration.getVariableValuePattern.matchEntire(str)!!.groupValues[1]
            if (symbol !in outerScope) {
                config.logger.error(Exception("undefined symbol: $symbol"))
                str
            } else outerScope[symbol]
        } else if (MacroConfiguration.macroDefinePattern.matches(str)) {
            val spl = str.split(":")
            val name = Regex("macro\\s+[a-zA-Z_]\\w*").find(str)!!.value.replace(Regex("macro\\s+|\\s*"), "")
            val params = spl[0].replace(Regex("macro\\s+[a-zA-Z_]\\w*|\\s*"), "").split(",")
            val body = spl.subList(1, spl.size).joinToString("")
            macroScope[name] = params to body
            ""
        } else if (MacroConfiguration.macroUsePattern.matches(str)) {
            val name = MacroConfiguration.macroUsePattern.matchEntire(str)!!.groupValues[1]
            val arguments = str.replace(Regex("!$name\\s+"), "").split(",").toMutableList()
            if (macroScope.contains(name)) {
                val params = macroScope[name]!!.first
                var body = macroScope[name]!!.second
                params.forEach {
                    body = body.replace("@[$it]", if (arguments.isEmpty()) {
                        config.logger.error(Exception("missing param: $it"))
                        ""
                    } else arguments.removeFirst())
                }
                body
            } else {
                config.logger.error(Exception("undefined macro: $name"))
                str
            }
        } else if (MacroConfiguration.ifDefinePattern.matches(str)) {
            val name = MacroConfiguration.ifDefinePattern.matchEntire(str)!!.groupValues[1]
            val body = str.replace(Regex("ifdef\\s+$name\\s+"), "")
            if (outerScope.contains(name)) body else ""
        } else if (MacroConfiguration.ifNotDefinePattern.matches(str)) {
            val name = MacroConfiguration.ifNotDefinePattern.matchEntire(str)!!.groupValues[1]
            val body = str.replace(Regex("if!def\\s+$name\\s+"), "")
            if (!outerScope.contains(name)) body else ""
        } else if (MacroConfiguration.repeatPattern.matches(str)) {
            val times = MacroConfiguration.repeatPattern.matchEntire(str)!!.groupValues[1].toInt()
            val body = str.replace(Regex("repeat\\s+\\d+\\s*:"), "")
            val result = StringBuilder()
            for (i in 0 until times) {
                result.append(body)
            }
            result
        } else if (MacroConfiguration.includePattern.matches(str)) {
            if (config.recursionCount > config.recursionLimit) throw Exception("stack overflow, the limit is ${config.recursionLimit} while launching this macro")
            config.recursionCount ++
            macro(config.fetch(str.replace(Regex("include\\s+"), "")), config)
        } else if (MacroConfiguration.commentPattern.matches(str)) {
            ""
        } else if (MacroConfiguration.velocityPattern.matches(str)) {
            // 音名序列可用, 和弦使用会出bug

            val funcName = MacroConfiguration.velocityPattern.matchEntire(str)!!.groupValues[1].trim()
            val range = MacroConfiguration.velocityPattern.matchEntire(str)!!.groupValues[2].replace(Regex("\\s"), "").split("~")
            val body = str.replace(Regex("velocity\\s+(linear\\s|func\\s)(\\d{1,3}\\s*~\\s*\\d{1,3})\\s*:"), "")

            if (range[0] == "100" && range[1] == "100") body else {
                when (funcName) {
                    "linear" -> {
                        val result = Regex("([abcdefgABCDEFG~^vmwnui!pqsz])").findAll(body).toList()
                        val ret = StringBuilder()
                        val from = range[0].toInt()
                        val to = range[1].toInt()
                        if (result.isEmpty()) throw Exception("body has to contain notes")
                        val step = (to - from).toDouble() / result.size
                        var count = .0

                        body.forEach {
                            if (it in "abcdefgABCDEFG~^vmwnui!pqsz") {
                                ret.append("$it%${(from + count).toInt()}")
                                count += step
                            } else ret.append(it)
                        }
                        ret.toString()
                    }
                    else -> config.logger.error(Exception("unsupported function in velocity: $funcName"))
                }
            }
        } else {
            config.logger.error(Exception("unsupported operation in outer: $str"))
            str
        }
    }

    val stack = Stack<Char>()

    val buildStack = Stack<CharSequence>()
    seq.replace("?", "").forEach {
        if (it != ')') stack.push(it) else {
            val sb = StringBuilder()
            var stackChar: Char

            do {
                stackChar = stack.pop()
                sb.append(stackChar)
            } while (stackChar != '(')

            stack.push('?')

            buildStack.push(sb.toString().replaceFirst("(", ""))
        }
    }

    val innerBuildStack = Stack<CharSequence>()

    while (buildStack.isNotEmpty()) {
        val undetermined = buildStack.pop()
        val buildStackString = if (undetermined.contains('?')) {
            var tmp = undetermined
            for (i in 0 until charCount(tmp, '?')) {
                // todo 调整执行顺序
                val result = innerScopeExecute(buildStack.pop().toString().reversed())
                tmp = tmp.replaceFirst(Regex("\\?"), result.reversed())
            }
            tmp.reversed()
        } else undetermined.reversed()
        innerBuildStack.push(buildStackString)
    }

    val result = StringBuilder()

    stack.forEach {
        if (it == '?') {
            result.append(outerScopeExecute(innerBuildStack.pop().toString()))
        } else result.append(it)
    }

    return result.toString()
}

fun toInMusicScoreList(seq: String, pitch: Int = 4, isStave: Boolean = true, useMacro: Boolean = true, config: MacroConfiguration = MacroConfiguration()): List<InMusicScore> {

    val list = mutableListOf<InMusicScore>()
    val doAfter = mutableListOf<(Char)->Unit>()
    var skipper = 0 // 跳过多少个字符 0 表示不跳过

    val afterMacro = if (useMacro) macro(seq, config) else seq

    fun checkSuffixModifyAvailable() {
        if (list.isEmpty()) throw Exception("before modify or clone the note, you should insert at least one\ninput: $afterMacro\nisStave: $isStave")
    }

    fun cloneAndModify(times: Int = 1, isUpper: Boolean = true) {
        checkSuffixModifyAvailable()
        if (list.last() is Note) {
            if (isUpper)
                list += list.last().clone().cast<Note>().upperNoteName(times)
            else
                list += list.last().clone().cast<Note>().lowerNoteName(times)
        }
    }

    fun cloneAndModifyInChord(chord: Chord, times: Int = 1, isUpper: Boolean = true) {
        if (isUpper)
            chord += chord.last().clone().upperNoteName(times)
        else
            chord += chord.last().clone().lowerNoteName(times)
    }

    afterMacro.forEachIndexed { index, char ->

        if (skipper == 0) {
            when (char) {

                in 'a'..'g' -> {
                    if (isStave)
                        list += Note(char, pitch = pitch)
                    else if (char == 'b') {
                        doAfter += {
                            (list.last() as? Note)?.flap()
                        }
                    }
                }

                in 'A'..'G' -> {
                    if (isStave)
                        list += Note(char, pitch = pitch + 1)
                }

                in '0'..'9' -> {
                    if (isStave) {
                        checkSuffixModifyAvailable()
                        if (list.last() is Note)
                            list.last().cast<Note>().pitch = char.code - 48
                        else if (list.last() is Chord)
                            list.last().cast<Chord>().last().pitch = char.code - 48
                    } else if (char in '1'..'7') {
                        val note = Note('C', pitch = pitch)
                        note.sharp(deriveInterval(char.code - 49))
                        list += note
                    } else if (char == '0') {
                        doAfter.clear()
                        list += Rest()
                    }
                }

                'O' -> {
                    doAfter.clear()
                    list += Rest().let { it.duration.double; it }
                }

                'o' -> {
                    doAfter.clear()
                    list += Rest()
                }

                't' -> {
                    if (list.last() is Appoggiatura) {
                        list.last().cast<Appoggiatura>().isFront = false
                    }
                }

                '~' -> {
                    list += list.last().clone()
                }

                '^' -> cloneAndModify(1)
                'm' -> cloneAndModify(2)
                'n' -> cloneAndModify(3)
                'p' -> cloneAndModify(5)
                's' -> cloneAndModify(6)

                'v' -> cloneAndModify(1, false)
                'w' -> cloneAndModify(2, false)
                'u' -> cloneAndModify(3, false)
                'q' -> cloneAndModify(5, false)
                'z' -> cloneAndModify(6, false)

                'i' -> {
                    if (isStave) {
                        cloneAndModify(4)
                    } else {
                        checkSuffixModifyAvailable()
                        if (list.last() is Note)
                            list.last().cast<Note>() += 1
                        else if (list.last() is Chord)
                            list.last().cast<Chord>().last() += 1
                    }
                }

                '︴', '↟' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is Chord)
                        list.last().cast<Chord>().arpeggio = ArpeggioType.Ascending
                }

                '↡' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is Chord)
                        list.last().cast<Chord>().arpeggio = ArpeggioType.Downward
                }

                '↑', '∧' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is Note)
                        list.last().cast<Note>() += 1
                    else if (list.last() is Chord)
                        list.last().cast<Chord>().last() += 1
                }

                '!' -> {
                    if (isStave) {
                        cloneAndModify(4, false)
                    } else {
                        checkSuffixModifyAvailable()
                        if (list.last() is Note)
                            list.last().cast<Note>() -= 1
                        else if (list.last() is Chord)
                            list.last().cast<Chord>().last() -= 1
                    }
                }

                '↓', '∨' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is Note)
                        list.last().cast<Note>() -= 1
                    else if (list.last() is Chord)
                        list.last().cast<Chord>().last() -= 1
                }

                '#', '♯' -> {
                    doAfter += {
                        (list.last() as? Note)?.sharp()
                    }
                }

                '&', '♮' -> {
                    doAfter += {
                        if (list.last() is Note)
                            list.last().cast<Note>().isNature = true
                    }
                }

                '$', '♭' -> {
                    doAfter += {
                        if (list.last() is Note)
                            list.last().cast<Note>().flap()
                    }
                }

                '\'' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is Note)
                        list.last().cast<Note>().flap()
                    else if (list.last() is Chord)
                        list.last().cast<Chord>().last().flap()
                }

                '"' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is Note)
                        list.last().cast<Note>().sharp()
                    else if (list.last() is Chord)
                        list.last().cast<Chord>().last().sharp()
                }

                ':' -> {
                    if (list.isEmpty()) throw Exception("the root is necessary for creating a chord")

                    val chord: Chord = if (list.last() is Note) {
                        val c = Chord(list.removeLast().cast())
                        list += c
                        c
                    } else if (list.last() is Chord) {
                        list.last().cast()
                    } else throw Exception("build chord failed: unsupported type: ${list.last()}")

                    doAfter += {
                        when(it) {
                            '^' -> cloneAndModifyInChord(chord, 1)
                            'm' -> cloneAndModifyInChord(chord, 2)
                            'n' -> cloneAndModifyInChord(chord, 3)
                            'p' -> cloneAndModifyInChord(chord, 4)
                            'i' -> {
                                if (isStave)
                                    cloneAndModifyInChord(chord, 5)
                            }
                            's' -> cloneAndModifyInChord(chord, 6)

                            'v' -> cloneAndModifyInChord(chord, 1, false)
                            'w' -> cloneAndModifyInChord(chord, 2, false)
                            'u' -> cloneAndModifyInChord(chord, 3, false)
                            'q' -> cloneAndModifyInChord(chord, 4, false)
                            '!' -> {
                                if (isStave)
                                    cloneAndModifyInChord(chord, 5, false)
                            }
                            'z' -> cloneAndModifyInChord(chord, 6, false)

                            else -> {
                                chord += list.removeLast().cast()
                            }
                        }
                    }
                }

                '*' -> {
                    checkSuffixModifyAvailable()
                    val times = afterMacro.nextOnlyInt(index, 5)
                    skipper = when (times) {
                        in 0..9 -> 1
                        in 10..99 -> 2
                        in 100..999 -> 3
                        in 1000..9999 -> 4
                        10000 -> 5
                        else -> throw Exception("only allow repeat 10000 times")
                    }

                    for (i in 0 until times - 1) {
                        list += list.last().clone()
                    }
                }

                '%' -> {
                    checkSuffixModifyAvailable()
                    val velocity = afterMacro.nextOnlyInt(index, 3)
                    skipper = when (velocity) {
                        in 0..9 -> 1
                        in 10..99 -> 2
                        in 100..127 -> 3
                        else -> throw Exception("velocity should in 0 ~ 127")
                    }

                    when (list.last()) {
                        is Note -> list.last().cast<Note>().velocity = velocity
                        is Chord -> list.last().cast<Chord>().last().velocity = velocity
                        is Appoggiatura -> list.last().cast<Appoggiatura>().second.velocity = velocity
                    }
                }

                '[' -> {
                    checkSuffixModifyAvailable()
                    val lyric = afterMacro.nextGivenChar(index, ']', 10)
                    skipper = lyric.count()

                    if (list.last() is Note) {
                        list.last().cast<Note>().attach = NoteAttach(lyric = lyric)
                    } else if (list.last() is Chord) {
                        list.last().cast<Chord>().attach = ChordAttach(lyric = lyric)
                    }
                }

                ';' -> {
                    if (list.isEmpty()) throw Exception("the main note is necessary for creating a appoggiatura")

                    if (list.last() !is Note) throw Exception("appoggiatura require a note")

                    val main = list.removeLast().cast<Note>()

                    doAfter += {
                        list += Appoggiatura(main, list.removeLast().cast())
                    }
                }

                '+' -> {
                    checkSuffixModifyAvailable()
                    when (list.last()) {
                        is Appoggiatura -> {
                            list.last().cast<Appoggiatura>().second.duration.double
                        }
                        else -> list.last().duration.double
                    }
                }

                '-' -> {
                    checkSuffixModifyAvailable()
                    when (list.last()) {
                        is Appoggiatura -> {
                            list.last().cast<Appoggiatura>().second.duration.halve
                        }
                        else -> list.last().duration.halve
                    }
                }

                '.' -> {
                    checkSuffixModifyAvailable()
                    when (list.last()) {
                        is Appoggiatura -> {
                            list.last().cast<Appoggiatura>().second.duration.point
                        }
                        else -> list.last().duration.point
                    }
                }
            }

            if (isStave) {
                when(char) {
                    in "abcdefgABCDEFG~^vmwnui!pqsz" -> {
                        doAfter.asReversed().forEach { it(char) }
                        doAfter.clear()
                    }
                }
            } else {
                when(char) {
                    in "1234567~^vmwnupqsz" -> {
                        doAfter.asReversed().forEach { it(char) }
                        doAfter.clear()
                    }
                }
            }

        } else if (skipper > 0) {
            skipper --
            // println("skip: $char, $index")
        } else throw Exception("skipper should not be negative")
    }

    return list
}

class MacroConfiguration(build: MacroConfigurationBuilder.() -> Unit = {}) {

    companion object {
        val variableNamePattern = Regex("([a-zA-Z_@]\\w*)")
        val getVariableValuePattern = Regex("=\\s*${variableNamePattern.pattern}\\s*")
        val definePattern = Regex("def\\s+${variableNamePattern.pattern}\\s*=\\s*[^>\\s][^>]*")
        val executePattern = Regex("def\\s+${variableNamePattern.pattern}\\s*:\\s*[^>\\s][^>]*")
        val macroDefinePattern = Regex("macro\\s+[a-zA-Z_]\\w*\\s+([a-zA-Z_]\\w*)(\\s*,\\s*([a-zA-Z_]\\w*))*\\s*:(\\s*[^>\\s][^>]*)")
        val macroUsePattern = Regex("!([a-zA-Z_]\\w*)\\s+[^>]+")
        val ifDefinePattern = Regex("ifdef\\s+([a-zA-Z_]\\w*)\\s+[^>]+")
        val ifNotDefinePattern = Regex("if!def\\s+([a-zA-Z_]\\w*)\\s+[^>]+")
        val repeatPattern = Regex("repeat\\s+(\\d+)\\s*:\\s*[^>]+")
        val includePattern = Regex("include\\s+((https?|ftp|file)://)?[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")
        val commentPattern = Regex("#\\s+[\\s\\S]+")
        val velocityPattern = Regex("velocity\\s+(linear\\s|func\\s)(\\d{1,3}\\s*~\\s*\\d{1,3})\\s*:\\s*[^>]+")
    }

    var recursionCount = 0 // 递归次数统计
    val logger: LoggerImpl = LoggerImpl()
    //    var useStrict = false
    var recursionLimit = 10
    var outerScope = mutableMapOf<String, String>()
    var macroScope = mutableMapOf<String, Pair<List<String>, String>>()

    var fetch: (String) -> String = {
        if (it.startsWith("file://"))
            File(it.replace("file://", "")).readText()
        else {
            URL(it).openStream().reader().readText()
        }
    }

    init {
        build(MacroConfigurationBuilder())
    }

    class LoggerImpl {
        var info: (String) -> Unit = { println("info>>$it") }
        var error: (Exception) -> Unit = { println("err>>$it") }
    }

    inner class MacroConfigurationBuilder {
        fun loggerInfo(block: (String)-> Unit) {
            logger.info = block
        }

        fun loggerError(block: (Exception)-> Unit) {
            logger.error = block
        }

        fun fetchMethod(block: (String)-> String) {
            fetch = block
        }

        fun setScopes(outer: MutableMap<String, String>, macro: MutableMap<String, Pair<List<String>, String>>) {
            outerScope = outer
            macroScope = macro
        }

        fun recursionLimit(times: Int) {
            recursionLimit = times
        }
    }
}

class MiderCodeParserConfiguration(build: Builder.() -> Unit = {}) {
    var formatMode: String = "internal->java-lame"
    var _isBlankReplaceWith0 = false
    var macroConfiguration = MacroConfiguration()

    inner class Builder {
        var isBlankReplaceWith0: Boolean = false
            set(value) {
                _isBlankReplaceWith0 = value
                field = value
            }

        fun macroConfig(block: MacroConfiguration.() -> Unit) {
            macroConfiguration.block()
        }
    }
}