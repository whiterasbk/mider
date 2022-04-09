package whiter.music.mider.dsl

import whiter.music.mider.EventType
import whiter.music.mider.MetaEventType
import whiter.music.mider.MidiFile
import whiter.music.mider.bpm
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.log2
import kotlin.math.*
import kotlin.reflect.KProperty

/**
 * 使用 mider dsl 产生音符并输出到指定文件
 * @param path 要保存到的路径
 * @param block 音符块
 */
fun apply(path: String, block: MiderDSL.() -> Any) {
    val mdsl = MiderDSL()
    val minimsTicks = 960
    val clock: Byte = 18
    with(mdsl, block)
    val midi = MidiFile()
    midi.append {
        track {
            meta(MetaEventType.META_TEMPO, args = bpm(mdsl.bpm))

            mdsl.keySignature?.let {
                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
            }

            mdsl.timeSignature?.let {
                meta(MetaEventType.META_TIME_SIGNATURE, it.first.toByte(), log2(it.second.toDouble()).toInt().toByte(), clock, 8)
            }

            meta(MetaEventType.META_END_OF_TRACK)
        }

        track {
            messaged(EventType.program_change, mdsl.program.id.toByte())

            for (i in mdsl.list) {
                val note = i.code
                messageno(note, 0, i.velocity)
                messagenf(note, (minimsTicks * 2 * i.duration).toInt(), i.velocity)
            }

            meta(MetaEventType.META_END_OF_TRACK)
        }
    }
    midi.save(path)
}

class MiderDSL {
    val list = mutableListOf<note>()
    private val i = listOf(I(0), I(2), I(4), I(5), I(7), I(9), I(11))
    private val entrusti = mutableMapOf<String, note>()
    private val entrustc = mutableMapOf<String, MutableList<note>>()
    private val __rest_instance = rest()
    private val current: note get() = list[list.lastIndex]
    private val last: note get() = list[list.lastIndex - 1]
    val major = 0
    val minor = 1
    /**
     * 大调音阶
     */
    val majorScale = arrayOf(2, 2, 1, 2, 2, 2, 1)
    /**
     * 小调音阶
     */
    val minorScale = arrayOf(2, 1, 2, 2, 1, 2, 2)

    /**
     * 可以用于构建大三和弦, 大七和弦和大九和弦
     * 使用方法: `val symbol by C triad majorChord`
     */
    val majorChord = arrayOf(4, 3, 4, 3)
    /**
     * 可以用于构建小三和弦, 小七和弦, 小九和弦
     * 使用方法: `val symbol by C triad minorChord`
     */
    val minorChord = arrayOf(3, 4, 3, 4)
    // 属七和弦 属大九和弦
    val dominant = arrayOf(4, 3, 3, 4)
    // 属小九和弦
    val dominantMinorNinth = arrayOf(4, 3, 3, 3)
    // 增三和弦
    val augmentedChord = arrayOf(4, 4)
    // 减三和弦 半减七和弦
    val diminiITdChord = arrayOf(3, 3, 4)
    // 减七和弦
    val decreasedSeventh = arrayOf(3, 3, 3)

    var pitch: Byte = 4
    var duration = 1.0 // 1.0为全音符
    var defaultNoteDuration = 4 // 默认是四分音符
    var velocity: Byte = 100
    var program: instrument = instrument.piano
    var bpm = 80
    var timeSignature: Pair<Int, Int>? = null //= 4 to 4
    val signatureKeysList = mutableListOf<Pair<Pair<Ks, Int>, IntRange>>()
    var keySignature: Pair<Ks, Int>? = null // getKeySignatureFromN(note('C'), major)
    val end = 0

    /**
     * 开头不能是休止符
     */
    val O: rest get() {
        if (list.size == 0) throw Exception("rest note should not place at the beginning")
        current.duration += getRealDuration(duration)
        return __rest_instance
    }
    val C: I get() {
        push('C')
        return i[0]
    }
    val D: I get() {
        push('D')
        return i[1]
    }
    val E : I get() {
        push('E')
        return i[2]
    }
    val F : I get() {
        push('F')
        return i[3]
    }
    val G : I get() {
        push('G')
        return i[4]
    }
    val A : I get() {
        push('A')
        return i[5]
    }
    val B : I get() {
        push('B')
        return i[6]
    }
    // temp
    val T : I get() {
        return i.random()
    }

    /**
     * 执行一段代码并将其返回值解析成为音符
     * @receiver MiderDSL的实例, 方便调用dsl里的各种属性
     */
    operator fun (MiderDSL.() -> Any).not(): Any {
        val res = this()
        if (res is String) !res
        else if (res is Number) !res
        else if (res is BigInteger) -res
        else if (res is BigDecimal) -res
        return res
    }

    /**
     * 解析字符串为音符的语法糖
     * 字符串需要的语法规则见[parse]
     */
    operator fun String.not() = parse(this)

    /**
     * 将数字映射为音阶, 具体规则如下
     * 1~7对应c~b, 称为有效音阶; 0表示休止符号, 8表示升, 9表示降, ‘.’表示附点, 这四个符号称为操作符
     * 在8之后, 一个8表示升一个八度, 一个9表示升一个半音, 一个0表示增加一倍时长, 一个.表示增加为原来的1.5倍
     * 在9之后, 一个8表示降一个八度, 一个9表示降一个半音, 一个0表示降二分之一时长, 一个.表示当前时长除以1.5
     * 0在8和9之前, 表示增加一个当前时长
     * 有效音阶之后, 出现-则表示将当前音符时值设置为0
     * 如果单独一个8或9夹在有效音阶之间, 相当于88或者99
     * @receiver [Number] 要转换的数字
     */
    operator fun Number.not() = parseInt(toString())

    operator fun BigInteger.unaryMinus() = parseInt(toString())

    operator fun BigDecimal.unaryMinus() = parseInt(toString())

    fun parseInt(str: String) {
        var upFlag: Boolean? = null
        var symbolCount = 0

        (str + "1").forEach {
            when (it) {
                '1', '2', '3', '4', '5', '6', '7' -> {
                    C
                    current + derive(it.digitToInt() - 1, majorScale).toByte()
                    if (symbolCount == 1) {
                        if (upFlag == true) last + 12 else if (upFlag == false) last - 1
                    }
                    symbolCount = 0
                    upFlag = null
                }

                '0' -> {
                    if (list.size == 0) C

                    if (upFlag == null) {
                        current.duration += getRealDuration(duration)
                    } else if (upFlag == true) {
                        current.duration *= getRealDuration(duration)
                    } else {
                        current.duration /= getRealDuration(duration)
                        if (current.duration < 0) current.duration = .0
                    }
                }

                '8' -> {
                    symbolCount ++
                    if (list.size == 0) C
                    if (upFlag == null) {
                        upFlag = true
                    } else {
                        if (upFlag == true) {
                            current + 12
                        } else if (upFlag == false) {
                            current - 12
                        }
                    }
                }

                '9' -> {
                    symbolCount ++
                    if (list.size == 0) C
                    if (upFlag == null) {
                        upFlag = false
                    } else {
                        if (upFlag == true) {
                            current + 1
                        } else if (upFlag == false) {
                            current - 1
                        }
                    }
                }

                '.' -> {
                    if (list.size == 0) C
                    if (upFlag == null || upFlag == true) current * 1.5 else current / 1.5
                }

                '-' -> {
                    if (list.size == 0) C
                    current.duration = getRealDuration(.0)
                }
            }
        }

        pop()
    }

    /**
     * 在作用范围内使用给定音高
     * 用法:
     * ```kotlin
     * int {
     *  C..B
     * }
     * ```
     * @receiver 指定音高
     * @param block 音符块
     */
    operator fun Int.invoke(block: MiderDSL.() -> Any) {
        val _pitch = pitch
        pitch = this.toByte()
        !block
        pitch = _pitch
    }

    /**
     * 在作用范围内设定任意时值
     * 用法:
     * ```kotlin
     * double {
     *  C..B
     * }
     * ```
     * @receiver 指定时值
     * @param block 音符块
     */
    operator fun Double.invoke(block: MiderDSL.() -> Any) {
        '1' {
            val __duration = duration
            duration = this@invoke
            !block
            duration = __duration
            end
        }
    }

    /**
     * 在作用范围内设定音符时值, 比如'8'是在8分音符下; 之所以有这个函数是因为可以避免出现小数
     * 用法:
     * ```kotlin
     * char {
     *  C..B
     * }
     * ```
     * @receiver [Char] 指定音符时值
     * @param block 音符块
     */
    operator fun Char.invoke(block: MiderDSL.() -> Any) {
        if (this !in "123456789") throw Exception("can not set default note duration to $this, it should in 1-9")
        val _dnd = defaultNoteDuration
        defaultNoteDuration = this.digitToInt()
        !block
        defaultNoteDuration = _dnd
    }

    /**
     * 在作用范围内设定音符音高和时值
     * 用法:
     * ```kotlin
     * (int to double) {
     *  C..B
     * }
     * ```
     * @receiver [Pair] 指定音符音高和时值的配对时值
     * @param block 音符块
     */
    operator fun Pair<Int, Double>.invoke(block: MiderDSL.() -> Any) {
        this.first {
            this@invoke.second {
                '1' {
                    !block
                }
            }
        }
    }

    /**
     * 在作用范围内设定音符音高和时值; 其实只是想避免出现小数(
     * 用法:
     * ```kotlin
     * (int to char) {
     *  C..B
     * }
     * ```
     * @receiver [Pair] 指定音符音高和时值的配对时值
     * @param block 音符块
     */
    @JvmName("invokeIntChar")
    operator fun Pair<Int, Char>.invoke(block: MiderDSL.() -> Any) {
        this.first {
            this@invoke.second {
                !block
            }
        }
    }

    /**
     * 将作用范围内的音符转调(只进行大调之间的转换)
     * 用法:
     * ```kotlin
     * (Note to Note) {
     *  C..B
     * }
     * ```
     * @receiver [Pair] 第一个[I]是作用范围内的调; 第二个[I]是要转去的调
     * @param block 音符块
     */
    @JvmName("invokeII")
    operator fun Pair<I, I>.invoke(block: MiderDSL.() -> Any) {
        val to = first(major)
        val from = second(major)
        (from to to) (block)
    }

    /**
     * 将作用范围内的音符转调, 可以支持大调, 小调的互转; 转成的小调是大调的同名小调
     * 用法:
     * ```kotlin
     * (Note(mode) to Note(mode)) {
     *  C..B
     * }
     * ```
     * @receiver [Pair] 第一个[Pair]的[I]是作用范围内的调号, [Int]是大调还是小调; 第二个[Pair]同理
     * @param block 音符块
     */
    @JvmName("invokeKsIntKsInt")
    operator fun Pair<Pair<Ks, Int>, Pair<Ks, Int>>.invoke(block: MiderDSL.() -> Any): Any {
        if (first == second) {
            return !block
        }

        val root = first.first.code
        val third = root + derive(2, if (first.second == major) majorScale else minorScale)
        val sixth = root + derive(5, if (first.second == major) majorScale else minorScale)
        val seventh = root + derive(6, if (first.second == major) majorScale else minorScale)

        val dp = second.first.code - first.first.code
        val inserted = getInsertedNotes(block)
        inserted.first.forEach {
            if (it.sfn != SFNType.Natural) {

                val attach = if (it.code % 12 == third || it.code % 12 == sixth || it.code % 12 == seventh)
                    if (first.second == major && second.second == minor) -1
                    else if (first.second == minor && second.second == major) 1 else 0
                else 0

                it += (dp + attach).toByte()
            }
        }

        return inserted.second
    }

    // todo 使用大调还是小调
    fun use(mode: IntArray, root: note, block: MiderDSL.() -> Any) {
        getInsertedNotes(block).first.forEach {

        }
    }

    /**
     * 将作用范围内重复音符
     * 用法:
     * ```kotlin
     * repeat(2) {
     *  C..B
     * }
     * ```
     * @param times 重复次数, 默认为2
     * @param block 要重复的音符块
     */
    fun repeat(times: Int = 2, block: MiderDSL.() -> Any) {
        if (times <= 0) return
        for (i in 0 until times) !block
    }

    /**
     * 将作用范围内的音符包装为一个匿名函数; (多此一举了感觉是
     * 用法:
     * ```kotlin
     * val block = def {
     *  C..B
     * }
     *
     * block()
     * ```
     * @param block 音符块
     * @return 包含音符块的匿名函数
     */
    fun def(block: MiderDSL.() -> Any): MiderDSL.() -> Any = block

    fun velocity(v: Byte, block: MiderDSL.() -> Any) {
        val _velocity = velocity
        velocity = v
        !block
        velocity = _velocity
    }

    /**
     * 标记默认调号
     * @param v 指定的调号
     * @param s 大调还是小调, 可选值有major和minor
     */
    fun keySignature(v: I, s: Int) {
        keySignature = getKeySignatureFromN(current, s)
        pop()
    }

    /**
     * 设置keySignature以后在keySignature下演奏音符, 默认为C大调
     * @param block 音符块
     */
    fun atMainKeySignature(block: MiderDSL.() -> Any): Any {
        return keySignature?.let {
            if (keySignature!!.first == Ks.C && keySignature!!.second == major)
                !block
            else
                (C(major) to it) (block)
        } ?: run {
            !block
        }
    }

    /**
     * 获取block执行期间插入的音符
     * @param block 音符块
     * @return [Pair].first: [List] 获取插入的音符块
     */
    private fun getInsertedNotes(block: MiderDSL.() -> Any): Pair<List<note>, Any> {
        val res = mutableListOf<note>()

        val start = list.size
        val aret = !block
        val end = list.size

        if (start == end) return listOf<note>() to aret // 返回空列表

        for (i in start until end) {
            res += list[i]
        }

        return res to aret
    }

    /**
     * 解析字符串为音符
     * 规则: 大体上与代码行间的规则一致
     * @param str 要解析的字符串
     * @param isChord 是否为和弦, 是递归参数, 不要使用默认值之外的值
     */
    fun parse(str: String, isChord: Boolean = false) {
        str.trim().replace("\n", " ").replace(";", " ").replace("  ", " ").split(" ").forEach {
            val length = it.length
            val first_letter = it[0]
            val iDuration = if (isChord) 0.0 else duration

            if (first_letter == 'O') {
                // rest
                if (list.size == 0) throw Exception("rest note should not place at the beginning")

                if (length == 1) {
                    current.duration += getRealDuration(duration)
                } else if (it[1] == '*') {
                    val v = it.substring(2 until it.length).toDouble()
                    current.duration += getRealDuration(duration) * v
                } else if (it[1] == '/') {
                    val v = it.substring(2 until it.length).toDouble()
                    current.duration += getRealDuration(duration) * (1.0 / v)
                } else throw Exception("parse failed")
            } else if (length == 1) {
                push(it[0], duration = iDuration)
            } else if (length == 2) {
                push(it[1], duration = iDuration, sfn = when(first_letter) {
                    '-', 'b' -> SFNType.Flat
                    '+', '#' -> SFNType.Sharp
                    '!' -> SFNType.Natural
                    else -> SFNType.Self
                })
            } else {
                if (it.contains(":")) {
                    it.split(":").forEachIndexed { i, e ->
                        parse(e, i != 0)
                    }
                } else {
                    var snf = SFNType.Self
                    var withoutsnf: String = it

                    if (first_letter in "-+!b#") {
                        snf = when(first_letter) {
                            '-', 'b' -> SFNType.Flat
                            '+', '#' -> SFNType.Sharp
                            '!' -> SFNType.Natural
                            else -> SFNType.Self
                        }
                        withoutsnf = it.substring(1 until length)
                    }

                    val noteName = withoutsnf[0]

                    if ('[' in withoutsnf) {
                        val args = withoutsnf.substring(1 until  withoutsnf.length).replace("[", "").replace("]", "").split(',')
                        if (args.size == 1) {
                            push(noteName, args[0].toInt().toByte(), getRealDuration(duration), snf)
                        } else if (args.size == 2) {
                            push(noteName, args[0].toInt().toByte(), getRealDuration(args[1].toDouble()), snf)
                        } else throw Exception("parse failed")
                    } else if ('+' in withoutsnf || '*' in withoutsnf || '-' in withoutsnf || '/' in withoutsnf) {
                        val d = if ('*' in withoutsnf)
                            (withoutsnf.split('*')[1].split('+')[0].split('-')[0]).toDouble()
                        else if ('/' in withoutsnf) 1.0 / ((withoutsnf.split('/')[1].split('+')[0].split('-')[0]).toDouble()) else iDuration

                        val p = if ('+' in withoutsnf)
                            (withoutsnf.split('+')[1].split('*')[0].split('/')[0]).toInt().toByte()
                        else if ('-' in withoutsnf) (-(withoutsnf.split('-')[1].split('*')[0].split('/')[0]).toInt()).toByte() else 0

                        push(noteName, (pitch + p).toByte(), d, snf)
                    } else throw Exception("parse failed")
                }

            }

        }
    }

    private fun push(x: Char, pitch: Byte = this.pitch, duration: Double = this.duration, sfn: SFNType = SFNType.Self) {
        list.add(note(x, pitch, getRealDuration(duration), velocity, sfn))
    }

    private fun push(n: note) = list.add(n)

    private fun pop(n: Int = 1): MutableList<note> {
        val popList = mutableListOf<note>()
        for (i in 0 until n) {
            popList += list.removeLast()
        }
        return popList
    }

    private fun getRealDuration(d: Double): Double {
        if (defaultNoteDuration == 1) return d
        if (d < 0) throw Exception("what if duration can be negative? anyway, now the duration of note should be positive")
        return d * (1.0 / defaultNoteDuration)
    }

    private fun insert(index: Int, x: Char, pitch: Byte = this.pitch, duration: Double = this.duration, sfn: SFNType = SFNType.Self) {
        list.add(index, note(x, pitch, getRealDuration(duration), velocity, sfn))
    }

    private fun insert(index: Int, n: note) = list.add(index, n)

    private fun fromNoteId(i: Byte): note {
        val n = note('C')
        n.code = i
        return n
    }


    companion object {

        // 推断距离根音音程
        private fun derive(index: Int, scale: Array<Int>): Int {
            var sum = 0
            for (i in 0 until index) {
                sum += scale[i]
            }
            return sum
        }

        private fun nextNoteName(s: Char): Char = when(s) {
            'C' -> 'D'
            'D' -> 'E'
            'E' -> 'F'
            'F' -> 'G'
            'G' -> 'A'
            'A' -> 'B'
            'B' -> 'C'
            else -> throw Exception("$s not in CDEFGAB")
        }

        private fun previousNoteName(s: Char): Char = when(s) {
            'C' -> 'B'
            'D' -> 'C'
            'E' -> 'D'
            'F' -> 'E'
            'G' -> 'F'
            'A' -> 'G'
            'B' -> 'A'
            else -> throw Exception("$s not in CDEFGAB")
        }

        private fun getKeySignatureFromN(n: note, s: Int): Pair<Ks, Int> {
            val nsk = Exception("no such signature key")
            return when(n.name) {
                'C' -> {
                    if (n.sfn == SFNType.Sharp) Ks.`#C`
                    else if (n.sfn == SFNType.Flat) Ks.bC
                    else if (n.sfn == SFNType.Self) Ks.C
                    else throw nsk
                }
                'D' -> {
                    if (n.sfn == SFNType.Flat) Ks.bD
                    else if (n.sfn == SFNType.Self) Ks.D
                    else throw nsk
                }
                'E' -> {
                    if (n.sfn == SFNType.Flat) Ks.bE
                    else if (n.sfn == SFNType.Self) Ks.E
                    else throw nsk
                }
                'F' -> {
                    if (n.sfn == SFNType.Sharp) Ks.`#F`
                    else if (n.sfn == SFNType.Self) Ks.F
                    else throw nsk
                }
                'G' -> {
                    if (n.sfn == SFNType.Flat) Ks.bG
                    else if (n.sfn == SFNType.Self) Ks.E
                    else throw nsk
                }
                'A' -> {
                    if (n.sfn == SFNType.Flat) Ks.bA
                    else if (n.sfn == SFNType.Self) Ks.A
                    else throw nsk
                }
                'B' -> {
                    if (n.sfn == SFNType.Flat) Ks.bB
                    else if (n.sfn == SFNType.Self) Ks.B
                    else throw nsk
                }
                else -> throw nsk
            } to s
        }

        private fun getNoteBasicOffset(name: Char): Byte = when(name){
            'C' -> 0
            'D' -> 2
            'E' -> 4
            'F' -> 5
            'G' -> 7
            'A' -> 9
            'B' -> 11
            else -> throw Exception("no such note")
        }

        private fun <T> List<List<T>>.merge(): List<T> {
            val temp = mutableListOf<T>()
            forEach {
                temp.addAll(it)
            }
            return temp
        }

        private fun MutableList<note>.clone(): MutableList<note> {
            val res = mutableListOf<note>()
            this.forEach {
                res += it.clone()
            }
            return res
        }

        @JvmName("clonenote")
        private fun List<note>.clone(): List<note> {
            val res = mutableListOf<note>()
            this.forEach {
                res += it.clone()
            }
            return res
        }
    }

    // key signature
    enum class Ks(val semitone: Int, val code: Int) {
        C(0, 0), G(1, 7), D(2, 2), A(3, 9), E(4, 4), B(5, 11), `#F`(6, 6),
        `#C`(7, 1), F(-1, 5), bB(-2, 10), bE(-3, 3), bA(-4, 8), bD(-5, 1), bG(-6, 6), bC(-7, 11)
    }

    enum class instrument(val id: Int) {
        piano(0), eletricgrandpiano(3),  musicbox(11), marimba(13), accordion(22),
        harmonica(23), nylongitar(25), acousicbass(33),
        violin(41), viola(42), cello(43), trumpet(57),
        trombone(58), tuba(59), sopranosax(65), altosax(66),
        tenorsax(67), barisax(68), oboe(69), piccolo(73),
        flute(74), recorder(75), whistle(79), kalimba(109),
        koto(108), fiddle(111), tinklebell(113)
    }

    inner class note(var name: Char, var pitch: Byte = this@MiderDSL.pitch, var duration: Double = getRealDuration(this@MiderDSL.duration), var velocity: Byte = this@MiderDSL.velocity, var sfn: SFNType = SFNType.Self): Cloneable {

        init {
            if (name !in "CDEFGAB") throw Exception("unsupport note: $name")
            if (duration < 0) throw Exception("duration: $duration has to > 0")
        }

        var code: Byte = 0
            get() {

                val id = getNoteBasicOffset(name) + when(sfn) {
                    SFNType.Sharp -> 1
                    SFNType.Flat -> -1
                    else -> 0
                } + (pitch + 1) * 12

                if (id < 0 || id > 128) throw Exception("no such note")

                return id.toByte()
            }

            set(value) {

                sfn = SFNType.Self

                when(value % 12) {
                    0 -> name = 'C'
                    1 -> { name = 'C'; sfn = SFNType.Sharp }
                    2 -> name = 'D'
                    3 -> { name = 'D'; sfn = SFNType.Sharp }
                    4 -> name = 'E'
                    5 -> name = 'F'
                    6 -> { name = 'F'; sfn = SFNType.Sharp }
                    7 -> name = 'G'
                    8 -> { name = 'G'; sfn = SFNType.Sharp }
                    9 -> name = 'A'
                    10 -> { name = 'A'; sfn = SFNType.Sharp }
                    11 -> name = 'B'
                }

                pitch = (value / 12 - 1).toByte()

                field = value
            }

        operator fun minusAssign(v: Byte) {
            code = abs((code - v) % 128).toByte()
        }

        operator fun plusAssign(v: Byte) {
            code = ((code + v) % 128).toByte()
        }

        operator fun plus(v: Byte): note {
            this += v
            return this
        }

        operator fun minus(v: Byte): note {
            this -= v
            return this
        }

        operator fun minus(v: note): Int = this.code - v.code

        operator fun times(d: Double): note {
            duration *= d
            return this
        }

        operator fun times(f: Float) = this * f.toDouble()

        operator fun times(i: Int) = this * i.toDouble()

        operator fun div(d: Double) = this * (1.0 / d)

        operator fun div(f: Float) = this * (1.0 / f)

        operator fun div(i: Int) = this * (1.0 / i)

        override fun toString(): String = "[${sfn.symbol}$name${pitch}|${duration}|$velocity]"

        public override fun clone(): note = super.clone() as note

    }

    enum class SFNType(val symbol: Char) {
        Flat('b'), Sharp('#'), Natural('@'), Self('&')
    }

    inner class Iin(val first: Int, val last: Int, val from: note, val to: note) {

        override fun toString(): String = "first: $first; last: $last; from: $from; to: $to"

        fun clear() {
            for (i in first..(last + 1)) {
                list.removeLast()
            }
        }

        infix fun step(v: Byte) {
            clear()
            for (i in from.code until (to.code) step v.toInt()) {
                push(fromNoteId(i.toByte()))
            }
        }

        infix fun under(v: Array<Int>) {
            clear()
            var i = from.code.toInt()
            var loopc = 0
            while (i <= to.code) {
                push(fromNoteId(i.toByte()))
                i += v[loopc % v.size]
                loopc++
            }
        }
    }

    inner class rest {
        operator fun times(v: Double) {
            current.duration -= getRealDuration(duration)
            current.duration += getRealDuration(duration) * v
        }

        operator fun times(v: Float) = this * v.toDouble()

        operator fun times(v: Int) = this * v.toDouble()

        operator fun div(v: Double) = this * (1.0 / v)

        operator fun div(v: Float) = this / v.toDouble()

        operator fun div(v: Int) = this / v.toDouble()
    }

    inner class chord(vararg notes: note) : Cloneable {
        constructor(vararg chords: chord) : this(*chords.map { it.note_list }.merge().toTypedArray())
        constructor(c: chord, vararg ns: note) : this(*(c.note_list.clone() + ns).toTypedArray())

        val note_list = notes.toMutableList()
        private val rest_note_list: MutableList<note> get() {
            val tempList = mutableListOf<note>()
            for (i in 1 until note_list.size)
                tempList += note_list[i]
            return tempList
        }

        val main: note get() {
            return getChordNotesFromList()[0]
            // note_list[0]
        }

        val second: note get() = note_list[1]
        val init_duration: Double = main.duration

        init {
            if (note_list.size < 2) throw Exception("a chord require at least 2 notes")
            for (i in 1 until note_list.size) {
                note_list[i].duration = getRealDuration(.0)
            }
            //note_list[1].duration = getRealDuration(.0)
        }

        operator fun get(index: Int): note = getChordNotesFromList()[index]

        operator fun plus(v: I): chord {
            current.duration = getRealDuration(.0)
            note_list.add(current)
            return this
        }

        operator fun plus(pitch: Int): chord {
            if (pitch != 0) {
                getChordNotesFromList().forEach {
                    it.pitch = (it.pitch + pitch).toByte()
                }

                note_list.forEach {
                    it.pitch = (it.pitch + pitch).toByte()
                }
            }
            return this
        }

        operator fun minus(pitch: Byte): chord = this + -pitch

        operator fun plus(c: chord): chord {
            val c2index = list.size - c.note_list.size
            list[c2index].duration = getRealDuration(.0)
            return chord(this, c)
        }

        operator fun times(v: Double): chord {
            main.duration = getRealDuration(v)
            note_list[0].duration = getRealDuration(v)
            return this
        }

        operator fun times(v: Float): chord = this * v.toDouble()

        operator fun times(v: Int): chord = this * v.toDouble()

        operator fun div(v: Double): chord = this * (1.0 / v)

        operator fun div(v: Float): chord = this / v.toDouble()

        operator fun div(v: Int): chord = this / v.toDouble()

        private fun getChordNotesFromList(): List<note> {
            return list.filterIndexed { index, _ ->
                index >= list.size - note_list.size
            }
        }

        operator fun div(v: I): chord {
            val bass = current.code
            val pList = pop()
            val chord_codes = getChordNotesFromList()
            val notes_codes = chord_codes.map { it.code }
            if (bass !in notes_codes) throw Exception("given note: ${pList[0].name} not in chord: $this")

            chord_codes.forEach {
                if (it.code < bass) {
                    it.pitch++
                }
            }
            return this
        }

        val sus4: chord get() {
            val chord_notes = getChordNotesFromList()
            val second = chord_notes[1]
            when(second.code - main.code) {
                4 -> second.code ++
                3 -> second.code = (second.code + 2).toByte()
                else -> {
                    throw Exception("this chord did not contain a three degree note from root")
                }
            }
            return chord(*chord_notes.clone().toTypedArray())
        }

        val sus: chord get() = sus4

        val sus2: chord get() {
            val chord_notes = getChordNotesFromList()
            val second = chord_notes[1]
            when(second.code - main.code) {
                4 -> second.code = (second.code - 2).toByte()
                3 -> second.code--
                else -> {
                    throw Exception("this chord did not contain a three degree note from root")
                }
            }

            return chord(*chord_notes.clone().toTypedArray())
        }

        operator fun getValue(nothing: Nothing?, property: KProperty<*>): chord {
            entrustc[property.name]?.let { list += it.clone() } ?: throw Exception("id ${property.name} is miss match")
            return this
        }

        infix fun into(id: String): chord {
            entrustc[id] = note_list.clone()
//            list -= note_list.toSet()
            pop(note_list.size)
            return this
        }

        override fun toString(): String = "<$note_list>"
    }

    inner class I(val id: Byte) {

        constructor(n: Char) : this(getNoteBasicOffset(n))

        init {
            // if (id !in byteArrayOf(0, 2, 4, 5, 7, 9, 11)); // do sth.?
        }

        operator fun getValue(nothing: Nothing?, property: KProperty<*>): I {
            entrusti[property.name]?.let { push(it) } ?: throw Exception("id ${property.name} is miss match")
            return this
        }

        // todo 获得关系小调
        infix fun relative(block: MiderDSL.() -> Any) {
            !block
        }

        // 获取同名小调
        infix fun minor(block: MiderDSL.() -> Any) {
            atMainKeySignature {

            }
        }

        infix fun into(id: String): I {
            entrusti[id] = current.clone()
            pop()
            return this
        }

        // inline operator fun rem(name: String) = this into name

        operator fun invoke(mf: Int): Pair<Ks, Int> {
            val res = getKeySignatureFromN(current, mf)
            pop()
            return res
        }

        operator fun invoke(mf: Int = major, block: MiderDSL.() -> Any) {
            val tothat = this(mf)
            val originKeySignature = keySignature
            keySignature?.let {
                keySignature = tothat
                (it to tothat) (block)
            } ?: run {
                keySignature = tothat
                (C(major) to tothat)(block)
            }
            keySignature = originKeySignature
        }

        operator fun get(pitch: Byte, duration: Double = this@MiderDSL.duration) : I {
            if (pitch != 4.toByte()) {
                current.pitch = pitch
            }
            current.duration = getRealDuration(duration)
            return this
        }

        operator fun get(pitch: Byte, duration: Int): I = this[pitch, duration.toDouble()]

        operator fun get(pitch: Byte, duration: Float): I = this[pitch, duration.toDouble()]

        override fun toString(): String = "大弦嘈嘈如急雨，小弦切切如私语。嘈嘈切切错杂弹，大珠小珠落玉盘。"

        operator fun not(): I {
            current.sfn = SFNType.Natural
            return this
        }

        infix fun up(v: Int): I {
            current += v.toByte()
            return this
        }

        infix fun down(v: Int): I {
            current -= v.toByte()
            return this
        }

        val dot: I get() = this dot 1

        infix fun dot(v: Int): I {
            current.duration *= Math.pow(1.5, v.toDouble())
            return this
        }

        // 构建三和弦
        infix fun triad(mode: Array<Int>): chord {
            // todo 根据调性
            return atMainKeySignature {
                val second = current.clone()
                second.duration = getRealDuration(.0)
                second += mode[0].toByte()
                val third = second.clone()
                third += mode[1].toByte()
                val chord = chord(current, second, third)
                push(second.clone())
                push(third.clone())
                chord
            } as chord
        }

        // 构建七和弦
        infix fun seventh(mode: Array<Int>): chord {
            val tr = T triad mode
            return atMainKeySignature {
                val fourth = tr.note_list[2].clone()
                fourth += mode[2].toByte()
                push(fourth.clone())
                chord(tr, fourth)
            } as chord
        }

        val addNinth: chord get() {
            val tr = T triad majorChord
            return atMainKeySignature {
                val fourth = tr.note_list[0].clone()
                fourth.duration = getRealDuration(.0)
                fourth += 14.toByte()
                push(fourth.clone())
                chord(tr, fourth)
            } as chord
        }

        val add9: chord get() = addNinth

        // 构建九和弦
        infix fun ninths(mode: Array<Int>): chord {
            val tr = T seventh mode
            return atMainKeySignature {
                val fifth = tr.note_list[3].clone()
                fifth += mode[3].toByte()
                push(fifth.clone())
                chord(tr, fifth)
            } as chord
        }


        operator fun rangeTo(x: I) : Iin {
            val lastIndex = list.lastIndex

            val from = last
            val to = current

            if (from.code > to.code) throw Exception("from.code has to > to.code")

            pop(2)

            for (i in from.code..to.code) {
                push(fromNoteId(i.toByte()))
            }

            return Iin(lastIndex, list.lastIndex, from, to)
        }

        operator fun unaryPlus() : I {
            current.sfn = SFNType.Sharp
            return this
        }

        operator fun unaryMinus(): I {
            current.sfn = SFNType.Flat
            return this
        }

        operator fun plus(x: Byte) : I {
            val origin = current.pitch
            current.pitch = (x + origin).toByte()
            return this
        }

        operator fun plus(x: I): chord = chord(last, current)

        operator fun plusAssign(x: Byte) {
            current += x
        }

        operator fun minus(x: Byte) : I {
            val origin = current.pitch
            current.pitch = (origin - x).toByte()
            return this
        }

        operator fun minusAssign(x: Byte) {
            current -= x
        }

        operator fun times(x: Double) : I {
            current.duration = getRealDuration(x)
            return this
        }

        operator fun times(x: Int) : I = this * x.toDouble()

        operator fun times(x: Float) : I = this * x.toDouble()

        operator fun div(x: Double) : I = this * (1.0 / x)

        operator fun div(x: Float) : I = this / x.toDouble()

        operator fun div(x: Int) : I = this / x.toDouble()

        infix fun C(x: I): I {
            insert(list.lastIndex, 'C')
            return i[0]
        }

        infix fun D(x: I): I {
            insert(list.lastIndex, 'D')
            return i[1]
        }

        infix fun E(x: I): I {
            insert(list.lastIndex, 'E')
            return i[2]
        }

        infix fun F(x: I): I {
            insert(list.lastIndex, 'F')
            return i[3]
        }

        infix fun G(x: I): I {
            insert(list.lastIndex, 'G')
            return i[4]
        }

        infix fun A(x: I): I {
            insert(list.lastIndex, 'A')
            return i[5]
        }

        infix fun B(x: I): I {
            insert(list.lastIndex, 'B')
            return i[6]
        }

    }
}