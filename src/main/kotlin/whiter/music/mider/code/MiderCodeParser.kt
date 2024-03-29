package whiter.music.mider.code

import whiter.music.mider.*
import whiter.music.mider.descr.*
import whiter.music.mider.descr.Note
import whiter.music.mider.lyric.multiLyricResolve
import java.lang.StringBuilder
import java.util.*
import java.io.File
import java.net.URL

@Deprecated(message = "will be delete")
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
            for (i in 0 until tmp.charCount('?')) {
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

class ActionStack <P, R> : Stack<(P) -> R>() {
    operator fun plusAssign(block: (P) -> R) {
        push(block)
    }

    operator fun invoke(p: P): MutableList<R> {
        val resultSet = mutableListOf<R>()
        while (isNotEmpty()) resultSet += pop()(p)
        return resultSet
    }
}

fun toInMusicScoreList(seq: String, iPitch: Int = 4, iVelocity: Int = 100, iOnVelocity: Int = iVelocity, iOffVelocity: Int = iVelocity, iDurationDefault: Double = .25, iIsStave: Boolean = true, iDefaultChannel: Int? = null, iDefaultGap: RelativeTicks? = null, iUseMandarinOnomatopoeia: Boolean = true, useMacro: Boolean = true, config: MacroConfiguration = MacroConfiguration()): List<InMusicScore> {

    var pitch = iPitch
    var velocity = iVelocity
    var onVelocity = iOnVelocity
    var offVelocity = iOffVelocity
    var durationDefault = iDurationDefault
    var isStave = iIsStave
    var useMandarinOnomatopoeia = iUseMandarinOnomatopoeia
    var defaultChannel = iDefaultChannel
    var defaultGap = iDefaultGap

    val list = mutableListOf<InMusicScore>()
    val doAfter = ActionStack<Char, Unit>() //mutableListOf<(Char)->Unit>()
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
                    if (isStave) {
                        list += Note(char, pitch, DurationDescribe(default = durationDefault), velocity).apply {
                            if (onVelocity != velocity)
                                noteOnVelocity = onVelocity

                            if (offVelocity != velocity)
                                noteOffVelocity = offVelocity


                            defaultChannel?.let { attach = NoteAttach(channel = it) } ?: attach?.clearChannel()
//                                attach?.let { it.channel = null }

                            defaultGap?.let { attach = NoteAttach(gap = it) } ?: attach?.clearGap()
                        }
                    } else if (char == 'b') {
                        doAfter += {
                            (list.last() as? Note)?.flap()
                        }
                    }
                }

                in 'A'..'G' -> {
                    if (isStave) {
                        list += Note(char, pitch + 1, DurationDescribe(default = durationDefault), velocity).apply {
                            if (onVelocity != velocity)
                                noteOnVelocity = onVelocity

                            if (offVelocity != velocity)
                                noteOffVelocity = offVelocity

                            defaultChannel?.let { attach = NoteAttach(channel = it) } ?: attach?.clearChannel()

                            defaultGap?.let { attach = NoteAttach(gap = it) } ?: attach?.clearGap()
                        }
                    }
                }

                in "多哆瑞来米咪发唆嗦索拉啦稀西希" -> {
                    if (useMandarinOnomatopoeia) {
                        list += Note(when (char) {
                            '哆', '多' -> 'C'
                            '来', '瑞' -> 'D'
                            '咪', '米' -> 'E'
                            '发' -> 'F'
                            '唆', '嗦', '索' -> 'G'
                            '拉', '啦' -> 'A'
                            '稀', '西', '希' -> 'B'
                            else -> throw Exception("unsupported mandarin onomatopoeia: $char")
                        }, pitch, DurationDescribe(default = durationDefault), velocity).apply {
                            if (onVelocity != velocity)
                                noteOnVelocity = onVelocity

                            if (offVelocity != velocity)
                                noteOffVelocity = offVelocity

                            defaultChannel?.let { attach = NoteAttach(channel = it) } ?: attach?.clearChannel()

                            defaultGap?.let { attach = NoteAttach(gap = it) } ?: attach?.clearGap()
                        }
                    }

                // 加入中文拟声词支持确实不太符合规范
                // 但是 midercode 的目标是做到简单易用方便读写
                // 支不支持中文拟声词可有可无, 也不占用符号
                // 如果加入这一支持能使得第一次接触的人在输入方法和符号系统的障碍稍微小一点
                // 那么也算是对这套体系有所贡献吧
                }

                in '0'..'9' -> {
                    if (isStave) {
                        checkSuffixModifyAvailable()
                        if (list.last() is CanModifyTargetPitch)
                            list.last().cast<CanModifyTargetPitch>().modifyTargetPitch(char.code - 48)
                    } else if (char in '1'..'7') {
                        val note = Note('C', pitch, DurationDescribe(default = durationDefault), velocity).apply {
                            if (onVelocity != velocity)
                                noteOnVelocity = onVelocity

                            if (offVelocity != velocity)
                                noteOffVelocity = offVelocity

                            defaultChannel?.let { attach = NoteAttach(channel = it) } ?: attach?.clearChannel()
//                                attach?.let { it.channel = null }

                            defaultGap?.let { attach = NoteAttach(gap = it) } ?: attach?.clearGap()
                        }
                        note.up(deriveInterval(char.code - 49))
                        list += note
                    } else if (char == '0') {
                        doAfter.clear()
                        list += Rest(DurationDescribe(default = durationDefault))
                    }
                }

                'O' -> {
                    doAfter.clear()
                    list += Rest(DurationDescribe(default = durationDefault)).let { it.duration.double; it }
                }

                'o' -> {
                    doAfter.clear()
                    list += Rest(DurationDescribe(default = durationDefault))
                }

                't' -> {
                    checkSuffixModifyAvailable()
                    when (list.last()) {
                        is Appoggiatura -> {
                            list.last().cast<Appoggiatura>().isFront = false
                        }

                        is Glissando -> {
                            list.last().cast<Glissando>().isContainBlack = true
                        }

                        is Chord -> {
                            list.last().cast<Chord>().isIndependentDuration = true
                        }
                    }
                }

                '~' -> {
                    checkSuffixModifyAvailable()
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
                        if (list.last() is HasOctave) list.last().cast<HasOctave>().higherOctave()
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

                '↑', '∧', '高', 'N' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is HasOctave) list.last().cast<HasOctave>().higherOctave()
                }

                '!' -> {
                    if (isStave) {
                        cloneAndModify(4, false)
                    } else {
                        checkSuffixModifyAvailable()
                        if (list.last() is HasOctave) list.last().cast<HasOctave>().lowerOctave()
                    }
                }

                '↓', '∨', '低', 'V' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is HasOctave) list.last().cast<HasOctave>().lowerOctave()
                }

                '#', '♯', '升' -> {
                    doAfter += {
                        (list.last() as? HasFlatAndSharp)?.sharp()
                    }
                }

                '@', '♮' -> {
                    doAfter += {
                        if (list.last() is Note)
                            list.last().cast<Note>().isNature = true
                    }
                }

                '$', '♭', '降' -> {
                    doAfter += {
                        if (list.last() is Note)
                            list.last().cast<Note>().flap()
                    }
                }

                '\'' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is HasFlatAndSharp) list.last().cast<HasFlatAndSharp>().flap()
                }

                '"' -> {
                    checkSuffixModifyAvailable()
                    if (list.last() is HasFlatAndSharp) list.last().cast<HasFlatAndSharp>().sharp()
                }

                '&' -> {
                    if (list.isEmpty()) throw Exception("`&` requires at least 2 notes to combine.")

                    val tie: TieNote = if (list.last() is Note) {
                        val t = TieNote(list.removeLast().cast())
                        list += t
                        t
                    } else if (list.last() is TieNote) {
                        list.last().cast()
                    } else throw Exception("build tie failed: unsupported type: ${list.last()}")

                    doAfter += {
                        when (val beAdded = list.removeLast()) {
                            is Note -> tie += beAdded
                            else -> tie += Note("C", duration = beAdded.duration)
                        }
                    }
                }

                ':' -> {
                    if (list.isEmpty()) throw Exception("the root is necessary for creating a chord.")

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

                    val onOrOffOrNone = if (index + 1 in afterMacro.indices) {
                        afterMacro[index + 1]
                    } else throw Exception("% must be followed by [↑↓]?\\d{1,3}, not end of string")

                    val indexOffset = when (onOrOffOrNone) {
                        '↑', '↓' -> 1
                        in '0'..'9' -> 0
                        else -> throw Exception("% must be followed by [↑↓]?\\d{1,3}")
                    }

                    val givenVelocity = afterMacro.nextOnlyInt(index + indexOffset, 3)
                    skipper = when (givenVelocity) {
                        in 0..9 -> 1
                        in 10..99 -> 2
                        in 100..127 -> 3
                        else -> throw Exception("given: $givenVelocity, but velocity should in 0 ~ 127")
                    } + indexOffset

                    if (list.last() is CanModifyTargetVelocity)
                        when (onOrOffOrNone) {
                            '↑' -> list.last().cast<CanModifyTargetVelocity>().modifyTargetOffVelocity(givenVelocity)

                            '↓' -> list.last().cast<CanModifyTargetVelocity>().modifyTargetOnVelocity(givenVelocity)

                            else -> list.last().cast<CanModifyTargetVelocity>().modifyTargetVelocity(givenVelocity)
                        }
                }

                '[' -> {
                    checkSuffixModifyAvailable()
                    val lyric = afterMacro.nextGivenChar(index, ']', 1024)
                    skipper = lyric.count()
                    multiLyricResolve(lyric, list)
                }

                '{' -> {
                    val inBraces = afterMacro.nextGivenChar(index, '}', 1024)
                    skipper = inBraces.count()
                    when {
                        inBraces.startsWith("mark ") -> {
                            val content = inBraces.removePrefix("mark ")
                            content.split(",").forEach { item ->
                                val kv = item.split("=").map { it.trim() }
                                if (kv.size != 2) throw Exception("mark setup items group are expected like key=value concatenated with commas")
                                when (kv[0]) {
                                    "octave", "o" -> pitch = kv[1].toInt()
                                    "onVel", "onVelocity", "on" -> onVelocity = kv[1].toInt()
                                    "offVel", "offVelocity", "off" -> offVelocity = kv[1].toInt()
                                    "velocity", "vel", "v" -> {
                                        velocity = kv[1].toInt()
                                        onVelocity = velocity
                                        offVelocity = velocity
                                    }
                                    "stave", "s" -> isStave = kv[1].toBoolean()
                                    "mandarinOnomatopoeia", "moia" -> useMandarinOnomatopoeia = kv[1].toBoolean()
                                    "channel", "c" -> defaultChannel = kv[1].let {
                                        if (it == "default" || it == "null") null else it.toInt()
                                    }
                                    "baseDuration", "duration", "d" -> durationDefault = kv[1].toDouble()
                                    "gap" -> kv[1].let { v ->
                                        v.toLongOrNull()?.let { defaultGap = RelativeTicks(it) } ?: run {
                                            defaultGap = if (v == "default" || v == "null") null
                                            else RelativeTicks(v.durationSymbolsToMultiple())
                                        }
                                    }


                                    else -> println("unsupported mark setup.") // todo replace with logger.warning
                                }
                            }
                        }

                        else -> list += InMusicScoreEvent(inBraces, pitch, velocity, miderDefaultDuration = durationDefault) // assume that inBrace is hex data
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

                '=' -> {
                    if (list.isEmpty()) throw Exception("the root is necessary for creating a glissando")

                    val glissando: Glissando = if (list.last() is Note) {
                        val g = Glissando(list.removeLast().cast())
                        list += g
                        g
                    } else if (list.last() is Glissando) {
                        list.last().cast()
                    } else throw Exception("build glissando failed: unsupported type: ${list.last()}")

                    doAfter += {
                        glissando += list.removeLast().cast()
                    }
                }

                '≈' -> {
                    if (list.isEmpty()) throw Exception("the root is necessary for creating a glissando")

                    val glissando: Glissando = if (list.last() is Note) {
                        val g = Glissando(list.removeLast().cast())
                        g.isWave = true
                        list += g
                        g
                    } else if (list.last() is Glissando) {
                        list.last().cast()
                    } else throw Exception("build glissando failed: unsupported type: ${list.last()}")

                    doAfter += {
                        glissando += list.removeLast().cast()
                    }
                }

                '+' -> {
                    checkSuffixModifyAvailable()

                    if (list.last() is CanModifyTargetDuration)
                        list.last().cast<CanModifyTargetDuration>().getTargetDuration().double
                    else list.last().duration.double
                }

                '-' -> {
                    checkSuffixModifyAvailable()

                    if (list.last() is CanModifyTargetDuration)
                        list.last().cast<CanModifyTargetDuration>().getTargetDuration().halve
                    else list.last().duration.halve
                }

                '.' -> {
                    checkSuffixModifyAvailable()

                    if (list.last() is CanModifyTargetDuration)
                        list.last().cast<CanModifyTargetDuration>().getTargetDuration().point
                    else list.last().duration.point
                }

                '/' -> {
                    checkSuffixModifyAvailable()
                    val denominator = afterMacro.nextOnlyInt(index, 1).toDouble()
                    skipper = 1 // 跳过之后一位

                    if (list.last() is CanModifyTargetDuration)
                        list.last().cast<CanModifyTargetDuration>().getTargetDuration().denominator = denominator
                    else
                        list.last().duration.denominator = denominator
                }

                'x' -> {
                    checkSuffixModifyAvailable()
                    val multiple = afterMacro.nextOnlyInt(index, 1).toDouble()
                    skipper = 1 // 跳过之后一位

                    if (list.last() is CanModifyTargetDuration)
                        list.last().cast<CanModifyTargetDuration>().getTargetDuration().multiple = multiple
                    else
                        list.last().duration.multiple = multiple
                }
            }

            if (isStave) {
                when(char) {
                    in "abcdefgABCDEFG~^vmwnui!pqsz" -> {
                        doAfter(char)
                    }

                    in "多哆瑞来米咪发唆嗦索拉啦稀西希" -> {
                        if (useMandarinOnomatopoeia) doAfter(char)
                    }
                }
            } else {
                when(char) {
                    in "1234567~^vmwnupqsz" -> {
                        doAfter(char)
                    }

                    in "多哆瑞来米咪发唆嗦索拉啦稀西希" -> {
                        if (useMandarinOnomatopoeia) doAfter(char)
                    }
                }
            }

        } else if (skipper > 0) {
            skipper --
        } else throw Exception("skipper should not be negative")
    }

    return list
}

class MacroConfiguration {

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
    val logger: MacroConfigurationBuilder.LoggerImpl = MacroConfigurationBuilder.LoggerImpl()
    // var useStrict = false
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
}

class MacroConfigurationBuilder(private val config: MacroConfiguration = MacroConfiguration()) {
    fun loggerInfo(block: (String)-> Unit): MacroConfigurationBuilder {
        config.logger.info = block
        return this
    }

    fun loggerError(block: (Exception)-> Unit): MacroConfigurationBuilder {
        config.logger.error = block
        return this
    }

    fun fetchMethod(block: (String)-> String): MacroConfigurationBuilder {
        config.fetch = block
        return this
    }

    fun setScopes(outer: MutableMap<String, String>, macro: MutableMap<String, Pair<List<String>, String>>): MacroConfigurationBuilder {
        config.outerScope = outer
        config.macroScope = macro
        return this
    }

    fun recursionLimit(times: Int): MacroConfigurationBuilder {
        config.recursionLimit = times
        return this
    }

    class LoggerImpl {
        var info: (String) -> Unit = { println("info>>$it") }
        var error: (Exception) -> Unit = { println("err>>$it") }
    }

    fun build(): MacroConfiguration = config
}

data class MiderCodeParserConfiguration(
    var formatMode: String = "internal->java-lame",
    var isBlankReplaceWith0: Boolean = false,
    var macroConfiguration: MacroConfiguration = MacroConfiguration(),
    var convertMidiEventConfiguration: ConvertMidiEventConfiguration = ConvertMidiEventConfiguration()
)

class MiderCodeParserConfigurationBuilder(private val config: MiderCodeParserConfiguration = MiderCodeParserConfiguration()) {

    fun setFormatMode(fm: String): MiderCodeParserConfigurationBuilder {
        config.formatMode = fm
        return this
    }
    fun setIsBlankReplaceWith0(i0: Boolean): MiderCodeParserConfigurationBuilder {
        config.isBlankReplaceWith0 = i0
        return this
    }
    fun setMacroConfiguration(mc: MacroConfiguration): MiderCodeParserConfigurationBuilder {
        config.macroConfiguration = mc
        return this
    }
    fun setConvertMidiEventConfiguration(cmec: ConvertMidiEventConfiguration): MiderCodeParserConfigurationBuilder {
        config.convertMidiEventConfiguration = cmec
        return this
    }

    fun build(): MiderCodeParserConfiguration = config
}

fun getLyricAffectedNotes(list: List<InMusicScore>, lyrics: Int): List<InMusicScore> {
    var counter = 0
    val ret = mutableListOf<InMusicScore>()
    list.reversed().forEach {
        if (counter == lyrics) return@forEach

        when (it) {
            is Note, is Chord -> {
                ret += it
                counter ++
            }
        }
    }

    if (counter != lyrics) throw Exception("there are $lyrics note/chord need to pair lyric, but $counter provide.")
    return ret.reversed()
}
