import whiter.music.mider.MidiInstrument
import whiter.music.mider.code.produceCore
import whiter.music.mider.descr.NoteAttach
import whiter.music.mider.dsl.play
import javax.sound.midi.Instrument

fun main(args: Array<String>) {

//    val k = produceCore(">g>{onC}{offC}")
//    k.logs.forEach(::println)
    play {

//        program = MidiInstrument.oboe

        +"{ia=11}c[2]"
//        println("-------" + c.attach)
//        c.attach = NoteAttach("qw")
//        println("-------" + c.attach)
//        c.attach = NoteAttach(channel = 1)
//        println("-------" + c.attach)

//        +"c[好似]{onc}{offc++}"

        container.mainList.forEach(::println)
//        C;D;E
//        +"ax3"
//        produceCore("")

//        hex("i1=musicbox")
//        hex("on c")
//        hex("on d")
//        hex("on E")
//        hex("off c~")
//        hex("off d~")
//        hex("off E+")

//        hex("00 c0 21")
//        hex("00 90 45 64")
//        hex("83 60 80 45 64")
    }

//    play {
////        +"{:i 1} {:on bC2} {:off bc2,480}"
////        +"{00904564}{8360804564}"
//        hex("00 c0 21")
//        hex("00 90 45 64")
//        hex("83 60 80 45 64")
//    }


//    with(MDSL()) {
//        C D E F C+1 F D*5+1 F E D C*0; D+1
//        C D E F C+1 F D*5+1 F E D C*0; D+1
//        C; D; E; F; C+1; F; D*5+1; F; E; D; C*0; D+1
//
//        C[3]; C; D*6
//        C[.2]; C; +D
//
//        C..B step 2
//        C and B and D and G
//        G E E G D D G C C C D





//        (3 to 2.5) {
//
//        }
//
//        A {
//
//        }
//
//        (+B) {
//
//        }
//        !"""
//        +A-3*7.9 C[2] -D+2*0 +D*2.9-1
//        """
        // !"A+6:!D+1:A[.25]:C A"
//        val p = !"dv"

//        "Cm" {
//
//        }

//        C..B step 2



        //g4 a5  a =g+2
        //e5 f#6 f#=e+2
        //e5 f#6
        //g4 a5
        //d5 e6  e =d+2
        //d5 e6
        //g4 a5
        //c5 d6  d =d+2
        //c5 d6
        //c5 d6
        //d5 f#6
        //       c =b3+2

        // c  d  e  f  g  a  b    ==> C major
        // c# d# f  f# g# a# c
        // d  e  f# g  a  b  c#   ==> D major
        // d# e# g  g# a# c  d    ==>




        // e  f  f# g# a# c  d    ==>
        // g  f  f# g# a# c  d    ==>
        // a  f  f# g# a# c  d    ==>

//        val n = UI.note('G')
//        n -= 9
//        println(n)






//        '4' {
//            A; B; O/2; C; C/2+1; C*4; C[1,2]; O
//            "C O/2 B O*4"
//        }

//        5 {
////            defaultNoteDuration = 4
//            A; B/2; C/4; O/4
//            "A/8 B C"
//        }

//        velocity(78) {
//
//        }
//
//        repeat(2) {
//            F
//        }

//        D + 1
//        list.forEach {
//            println(it)
//        }
//    }
}

//class MDSL {
//    val list = mutableListOf<note>()
//    private val i = listOf(I(0), I(2), I(4), I(5), I(7), I(9), I(11))
//    private val entrusti = mutableMapOf<String, note>()
//    private val entrustc = mutableMapOf<String, MutableList<note>>()
//    private val __rest_instance = rest()
//    private val current: note get() = list[list.lastIndex]
//    private val last: note get() = list[list.lastIndex - 1]
//    val major = 0
//    val minor = 1
//    /**
//     * 大调音阶
//     */
//    val majorScale = arrayOf(2, 2, 1, 2, 2, 2, 1)
//    /**
//     * 小调音阶
//     */
//    val minorScale = arrayOf(2, 1, 2, 2, 1, 2, 2)
//
//    /**
//     * 可以用于构建大三和弦, 大七和弦和大九和弦
//     * 使用方法: `val symbol by C triad majorChord`
//     */
//    val majorChord = arrayOf(4, 3, 4, 3)
//    /**
//     * 可以用于构建小三和弦, 小七和弦, 小九和弦
//     * 使用方法: `val symbol by C triad minorChord`
//     */
//    val minorChord = arrayOf(3, 4, 3, 4)
//    // 属七和弦 属大九和弦
//    val dominant = arrayOf(4, 3, 3, 4)
//    // 属小九和弦
//    val dominantMinorNinth = arrayOf(4, 3, 3, 3)
//    // 增三和弦
//    val augmentedChord = arrayOf(4, 4)
//    // 减三和弦 半减七和弦
//    val diminiITdChord = arrayOf(3, 3, 4)
//    // 减七和弦
//    val decreasedSeventh = arrayOf(3, 3, 3)
//
//    var pitch: Byte = 4
//    var duration = 1.0 // 1.0为全音符
//    var defaultNoteDuration = 4 // 默认是四分音符
//    var velocity: Byte = 100
//    var program: instrument = instrument.piano
//    var bpm = 80
//    var timeSignature: Pair<Int, Int>? = null //= 4 to 4
//    val signatureKeysList = mutableListOf<Pair<Pair<Ks, Int>, IntRange>>()
//    var keySignature: Pair<Ks, Int>? = null // getKeySignatureFromN(note('C'), major)
//    val end = 0
//
//    /**
//     * 开头不能是休止符
//     */
//    val O: rest get() {
//        if (list.size == 0) throw Exception("rest note should not place at the beginning")
//        current.duration += getRealDuration(duration)
//        return __rest_instance
//    }
//    val C: I get() {
//        push('C')
//        return i[0]
//    }
//    val D: I get() {
//        push('D')
//        return i[1]
//    }
//    val E : I get() {
//        push('E')
//        return i[2]
//    }
//    val F : I get() {
//        push('F')
//        return i[3]
//    }
//    val G : I get() {
//        push('G')
//        return i[4]
//    }
//    val A : I get() {
//        push('A')
//        return i[5]
//    }
//    val B : I get() {
//        push('B')
//        return i[6]
//    }
//    // temp
//    val T : I get() {
//        return i.random()
//    }
//
//    /**
//     * 执行一段代码并将其返回值解析成为音符
//     * @receiver MDSL的实例, 方便调用dsl里的各种属性
//     */
//    operator fun (MDSL.() -> Any).not(): Any {
//        val res = this()
//        if (res is String) parse(res)
//        return res
//    }
//
//    /**
//     * 解析字符串为音符的语法糖
//     * 字符串需要的语法规则见[parse]
//     */
//    operator fun String.not() = parse(this)
//
//    operator fun Int.invoke(block: MDSL.() -> Any) {
//        val __pitch = pitch
//        pitch = this.toByte()
//        !block
//        pitch = __pitch
//    }
//
//    // 设定任意时值
//    operator fun Double.invoke(block: MDSL.() -> Any) {
//        '1' {
//            val __duration = duration
//            duration = this@invoke
//            !block
//            duration = __duration
//            end
//        }
//    }
//
//    // 设置默认音符时值, 这样可以避免出现小数（
//    operator fun Char.invoke(block: MDSL.() -> Any) {
//        if (this !in "123456789") throw Exception("can not set default note duration to $this, it should in 1-9")
//        val _dnd = defaultNoteDuration
//        defaultNoteDuration = this.digitToInt()
//        !block
//        defaultNoteDuration = _dnd
//    }
//
//    // 设定音高和任意时值
//    operator fun Pair<Int, Double>.invoke(block: MDSL.() -> Any) {
//        this.first {
//            this@invoke.second {
//                '1' {
//                    !block
//                }
//            }
//        }
//    }
//
//    // 其实只是想避免出现小数（
//    @JvmName("invokeIntChar")
//    operator fun Pair<Int, Char>.invoke(block: MDSL.() -> Any) {
//        this.first {
//            this@invoke.second {
//                !block
//            }
//        }
//    }
//
//    @JvmName("invokeII")
//    operator fun Pair<I, I>.invoke(block: MDSL.() -> Any) {
//        val to = first(major)
//        val from = second(major)
//        (from to to) (block)
//    }
//
//    // 转小调是转的同号小调
//    @JvmName("invokeKsIntKsInt")
//    operator fun Pair<Pair<Ks, Int>, Pair<Ks, Int>>.invoke(block: MDSL.() -> Any): Any {
//        if (first == second) {
//            return !block
//        }
//
//        val root = first.first.code
//        val third = root + derive(2, if (first.second == major) majorScale else minorScale)
//        val sixth = root + derive(5, if (first.second == major) majorScale else minorScale)
//        val seventh = root + derive(6, if (first.second == major) majorScale else minorScale)
//
//        val dp = second.first.code - first.first.code
//        val inserted = getInsertedNotes(block)
//        inserted.first.forEach {
//            if (it.sfn != SFNType.Natural) {
//
//                val attach = if (it.code % 12 == third || it.code % 12 == sixth || it.code % 12 == seventh)
//                    if (first.second == major && second.second == minor) -1
//                    else if (first.second == minor && second.second == major) 1 else 0
//                else 0
//
//                it += (dp + attach).toByte()
//            }
//        }
//
//        return inserted.second
//    }
//
//    // todo 使用大调还是小调
//    fun use(mode: IntArray, root: note, block: MDSL.() -> Any) {
//        getInsertedNotes(block).first.forEach {
//
//        }
//    }
//
//    fun repeat(times: Int, block: MDSL.() -> Any) {
//        if (times <= 0) return
//        for (i in 0 until times) !block
//    }
//
//    fun def(block: MDSL.() -> Any): MDSL.() -> Any = block
//
//    fun velocity(v: Byte, block: MDSL.() -> Any) {
//        val _velocity = velocity
//        velocity = v
//        !block
//        velocity = _velocity
//    }
//
//    // 标记默认调号
//    fun keySignature(v: I, s: Int) {
//        keySignature = getKeySignatureFromN(current, s)
//        pop()
//    }
//
//    fun atMainKeySignature(block: MDSL.() -> Any): Any {
//        return keySignature?.let {
//            if (keySignature!!.first == Ks.C && keySignature!!.second == major)
//                !block
//            else
//                (C(major) to it) (block)
//        } ?: run {
//            !block
//        }
//    }
//
//    // 获取block执行期间插入的音符
//    private fun getInsertedNotes(block: MDSL.() -> Any): Pair<List<note>, Any> {
//        val res = mutableListOf<note>()
//
//        val start = list.size
//        val aret = !block
//        val end = list.size
//
//        if (start == end) return listOf<note>() to aret // 返回空列表
//
//        for (i in start until end) {
//            res += list[i]
//        }
//
//        return res to aret
//    }
//
//    /**
//     * 解析字符串为音符
//     */
//    private fun parse(str: String, isChord: Boolean = false) {
//        str.trim().replace("\n", " ").replace(";", " ").replace("  ", " ").split(" ").forEach {
//            val length = it.length
//            val first_letter = it[0]
//            val iDuration = if (isChord) 0.0 else duration
//
//            if (first_letter == 'O') {
//                // rest
//                if (list.size == 0) throw Exception("rest note should not place at the beginning")
//
//                if (length == 1) {
//                    current.duration += getRealDuration(duration)
//                } else if (it[1] == '*') {
//                    val v = it.substring(2 until it.length).toDouble()
//                    current.duration += getRealDuration(duration) * v
//                } else if (it[1] == '/') {
//                    val v = it.substring(2 until it.length).toDouble()
//                    current.duration += getRealDuration(duration) * (1.0 / v)
//                } else throw Exception("parse failed")
//            } else if (length == 1) {
//                push(it[0], duration = iDuration)
//            } else if (length == 2) {
//                push(it[1], duration = iDuration, sfn = when(first_letter) {
//                    '-', 'b' -> SFNType.Flat
//                    '+', '#' -> SFNType.Sharp
//                    '!' -> SFNType.Natural
//                    else -> SFNType.Self
//                })
//            } else {
//                if (it.contains(":")) {
//                    it.split(":").forEachIndexed { i, e ->
//                        parse(e, i != 0)
//                    }
//                } else {
//                    var snf = SFNType.Self
//                    var withoutsnf: String = it
//
//                    if (first_letter in "-+!b#") {
//                        snf = when(first_letter) {
//                            '-', 'b' -> SFNType.Flat
//                            '+', '#' -> SFNType.Sharp
//                            '!' -> SFNType.Natural
//                            else -> SFNType.Self
//                        }
//                        withoutsnf = it.substring(1 until length)
//                    }
//
//                    val noteName = withoutsnf[0]
//
//                    if ('[' in withoutsnf) {
//                        val args = withoutsnf.substring(1 until  withoutsnf.length).replace("[", "").replace("]", "").split(',')
//                        if (args.size == 1) {
//                            push(noteName, args[0].toInt().toByte(), getRealDuration(duration), snf)
//                        } else if (args.size == 2) {
//                            push(noteName, args[0].toInt().toByte(), getRealDuration(args[1].toDouble()), snf)
//                        } else throw Exception("parse failed")
//                    } else if ('+' in withoutsnf || '*' in withoutsnf || '-' in withoutsnf || '/' in withoutsnf) {
//                        val d = if ('*' in withoutsnf)
//                                (withoutsnf.split('*')[1].split('+')[0].split('-')[0]).toDouble()
//                            else if ('/' in withoutsnf) 1.0 / ((withoutsnf.split('/')[1].split('+')[0].split('-')[0]).toDouble()) else iDuration
//
//                        val p = if ('+' in withoutsnf)
//                            (withoutsnf.split('+')[1].split('*')[0].split('/')[0]).toInt().toByte()
//                            else if ('-' in withoutsnf) (-(withoutsnf.split('-')[1].split('*')[0].split('/')[0]).toInt()).toByte() else 0
//
//                        push(noteName, (pitch + p).toByte(), d, snf)
//                    } else throw Exception("parse failed")
//                }
//
//            }
//
//        }
//    }
//
//    private fun push(x: Char, pitch: Byte = this.pitch, duration: Double = this.duration, sfn: SFNType = SFNType.Self) {
//        list.add(note(x, pitch, getRealDuration(duration), velocity, sfn))
//    }
//
//    private fun push(n: note) = list.add(n)
//
//    private fun pop(n: Int = 1): MutableList<note> {
//        val popList = mutableListOf<note>()
//        for (i in 0 until n) {
//            popList += list.removeLast()
//        }
//        return popList
//    }
//
//    private fun getRealDuration(d: Double): Double {
//        if (defaultNoteDuration == 1) return d
//        if (d < 0) throw Exception("what if duration can be negative? anyway, now the duration of note should be positive")
//        return d * (1.0 / defaultNoteDuration)
//    }
//
//    private fun insert(index: Int, x: Char, pitch: Byte = this.pitch, duration: Double = this.duration, sfn: SFNType = SFNType.Self) {
//        list.add(index, note(x, pitch, getRealDuration(duration), velocity, sfn))
//    }
//
//    private fun insert(index: Int, n: note) = list.add(index, n)
//
//    private fun fromNoteId(i: Byte): note {
//        val n = note('C')
//        n.code = i
//        return n
//    }
//
//
//    companion object {
//
//        // 推断距离根音音程
//        private fun derive(index: Int, scale: Array<Int>): Int {
//            var sum = 0
//            for (i in 0 until index) {
//                sum += scale[i]
//            }
//            return sum
//        }
//
//        private fun nextNoteName(s: Char): Char = when(s) {
//            'C' -> 'D'
//            'D' -> 'E'
//            'E' -> 'F'
//            'F' -> 'G'
//            'G' -> 'A'
//            'A' -> 'B'
//            'B' -> 'C'
//            else -> throw Exception("$s not in CDEFGAB")
//        }
//
//        private fun previousNoteName(s: Char): Char = when(s) {
//            'C' -> 'B'
//            'D' -> 'C'
//            'E' -> 'D'
//            'F' -> 'E'
//            'G' -> 'F'
//            'A' -> 'G'
//            'B' -> 'A'
//            else -> throw Exception("$s not in CDEFGAB")
//        }
//
//        private fun getKeySignatureFromN(n: note, s: Int): Pair<Ks, Int> {
////            val sf = when(n.name) {
////                'C' -> {
////                    if (n.sfn == SFNType.Sharp) 7 else 0
////                }
////                'D' -> {
////                    if (n.sfn == SFNType.Flat) -5 else 2
////                }
////                'E' -> {
////                    if (n.sfn == SFNType.Flat) -3 else 4
////                }
////                'F' -> {
////                    if (n.sfn == SFNType.Sharp) 6 else -1
////                }
////                'G' -> {
////                    if (n.sfn == SFNType.Flat) -6 else 1
////                }
////                'A' -> {
////                    if (n.sfn == SFNType.Flat) -4 else 3
////                }
////                'B' -> {
////                    if (n.sfn == SFNType.Flat) -2 else 5
////                }
////                else -> throw Exception("no such signature key")
////            }
//            val nsk = Exception("no such signature key")
//            return when(n.name) {
//                'C' -> {
//                    if (n.sfn == SFNType.Sharp) Ks.`#C`
//                    else if (n.sfn == SFNType.Flat) Ks.bC
//                    else if (n.sfn == SFNType.Self) Ks.C
//                    else throw nsk
//                }
//                'D' -> {
//                    if (n.sfn == SFNType.Flat) Ks.bD
//                    else if (n.sfn == SFNType.Self) Ks.D
//                    else throw nsk
//                }
//                'E' -> {
//                    if (n.sfn == SFNType.Flat) Ks.bE
//                    else if (n.sfn == SFNType.Self) Ks.E
//                    else throw nsk
//                }
//                'F' -> {
//                    if (n.sfn == SFNType.Sharp) Ks.`#F`
//                    else if (n.sfn == SFNType.Self) Ks.F
//                    else throw nsk
//                }
//                'G' -> {
//                    if (n.sfn == SFNType.Flat) Ks.bG
//                    else if (n.sfn == SFNType.Self) Ks.E
//                    else throw nsk
//                }
//                'A' -> {
//                    if (n.sfn == SFNType.Flat) Ks.bA
//                    else if (n.sfn == SFNType.Self) Ks.A
//                    else throw nsk
//                }
//                'B' -> {
//                    if (n.sfn == SFNType.Flat) Ks.bB
//                    else if (n.sfn == SFNType.Self) Ks.B
//                    else throw nsk
//                }
//                else -> throw nsk
//            } to s
//        }
//
//        private fun getNoteBasicOffset(name: Char): Byte = when(name){
//            'C' -> 0
//            'D' -> 2
//            'E' -> 4
//            'F' -> 5
//            'G' -> 7
//            'A' -> 9
//            'B' -> 11
//            else -> throw Exception("no such note")
//        }
//
//        private fun <T> List<List<T>>.merge(): List<T> {
//            val temp = mutableListOf<T>()
//            forEach {
//                temp.addAll(it)
//            }
//            return temp
//        }
//
//        private fun MutableList<note>.clone(): MutableList<note> {
//            val res = mutableListOf<note>()
//            this.forEach {
//                res += it.clone()
//            }
//            return res
//        }
//
//        @JvmName("clonenote")
//        private fun List<note>.clone(): List<note> {
//            val res = mutableListOf<note>()
//            this.forEach {
//                res += it.clone()
//            }
//            return res
//        }
//    }
//
//    // key signature
//    enum class Ks(val semitone: Int, val code: Int) {
//        C(0, 0), G(1, 7), D(2, 2), A(3, 9), E(4, 4), B(5, 11), `#F`(6, 6),
//        `#C`(7, 1), F(-1, 5), bB(-2, 10), bE(-3, 3), bA(-4, 8), bD(-5, 1), bG(-6, 6), bC(-7, 11)
//    }
//
//    enum class instrument(val id: Int) {
//        piano(0), eletricgrandpiano(3),  musicbox(11), marimba(13), accordion(22),
//        harmonica(23), nylongitar(25), acousicbass(33),
//        violin(41), viola(42), cello(43), trumpet(57),
//        trombone(58), tuba(59), sopranosax(65), altosax(66),
//        tenorsax(67), barisax(68), oboe(69), piccolo(73),
//        flute(74), recorder(75), whistle(79), kalimba(109),
//        koto(108), fiddle(111), tinklebell(113)
//    }
//
//    inner class note(var name: Char, var pitch: Byte = this@MDSL.pitch, var duration: Double = getRealDuration(this@MDSL.duration), var velocity: Byte = this@MDSL.velocity, var sfn: SFNType = SFNType.Self): Cloneable {
//
//        init {
//            if (name !in "CDEFGAB") throw Exception("unsupport note: $name")
//            if (duration < 0) throw Exception("duration: $duration has to > 0")
//        }
//
//        var code: Byte = 0
//            get() {
//
//                val id = getNoteBasicOffset(name) + when(sfn) {
//                    SFNType.Sharp -> 1
//                    SFNType.Flat -> -1
//                    else -> 0
//                } + (pitch + 1) * 12
//
//                if (id < 0 || id > 128) throw Exception("no such note")
//
//                return id.toByte()
//
////                val relname = when(sfn) {
////                    SFNType.Sharp -> {
////                        if (name in "EB") {
////                            nextNoteName(name)
////                        } else {
////                            name + "S"
////                        }
////                    }
////                    SFNType.Flat -> {
////                        previousNoteName(name)
////                    }
////                    else -> name
////                }.toString()
////
////                return Note.valueOf("$relname${
////                    when (sfn){
////                        SFNType.Flat -> {
////                            if (relname !in "BE") 'S' else ""
////                        }
////                        else -> ""
////                    }
////                }${pitch - when(sfn) {
////                    SFNType.Sharp -> {
////                        if (name == 'B') -1 else 0
////                    }
////                    SFNType.Flat -> {
////                        if (name == 'C') 1 else 0
////                    }
////                    else -> 0
////                }}").id
//            }
//
//            set(value) {
//
//                sfn = SFNType.Self
//
//                when(value % 12) {
//                    0 -> name = 'C'
//                    1 -> { name = 'C'; sfn = SFNType.Sharp }
//                    2 -> name = 'D'
//                    3 -> { name = 'D'; sfn = SFNType.Sharp }
//                    4 -> name = 'E'
//                    5 -> name = 'F'
//                    6 -> { name = 'F'; sfn = SFNType.Sharp }
//                    7 -> name = 'G'
//                    8 -> { name = 'G'; sfn = SFNType.Sharp }
//                    9 -> name = 'A'
//                    10 -> { name = 'A'; sfn = SFNType.Sharp }
//                    11 -> name = 'B'
//                }
//
//                pitch = (value / 12 - 1).toByte()
//
//                field = value
//            }
//
//        operator fun plusAssign(v: Byte) {
//            code = (code + v).toByte()
//        }
//
//        operator fun plus(v: Byte): note {
//            this += v
//            return this
//        }
//
//        operator fun minusAssign(v: Byte) {
//            code = (code - v).toByte()
//        }
//
//        operator fun minus(v: note): Int = this.code - v.code
//
//        operator fun times(d: Double): note {
//            duration *= d
//            return this
//        }
//
//        operator fun times(f: Float) = this * f.toDouble()
//
//        operator fun times(i: Int) = this * i.toDouble()
//
//        operator fun div(d: Double) = this * (1.0 / d)
//
//        operator fun div(f: Float) = this * (1.0 / f)
//
//        operator fun div(i: Int) = this * (1.0 / i)
//
//        override fun toString(): String = "[${sfn.symbol}$name${pitch}|${duration}|$velocity]"
//
//        public override fun clone(): note = super.clone() as note
//
//    }
//
//    enum class SFNType(val symbol: Char) {
//        Flat('b'), Sharp('#'), Natural('@'), Self('&')
//    }
//
//    inner class Iin(val first: Int, val last: Int, val from: note, val to: note) {
//
//        override fun toString(): String = "first: $first; last: $last; from: $from; to: $to"
//
//        fun clear() {
//            for (i in first..(last + 1)) {
//                list.removeLast()
//            }
//        }
//
//        infix fun step(v: Byte) {
//            clear()
//            for (i in from.code until (to.code) step v.toInt()) {
//                push(fromNoteId(i.toByte()))
//            }
//        }
//
//        infix fun under(v: Array<Int>) {
//            clear()
//            var i = from.code.toInt()
//            var loopc = 0
//            while (i <= to.code) {
//                push(fromNoteId(i.toByte()))
//                i += v[loopc % v.size]
//                loopc++
//            }
//        }
//    }
//
//    inner class rest {
//        operator fun times(v: Double) {
//            current.duration -= getRealDuration(duration)
//            current.duration += getRealDuration(duration) * v
//        }
//
//        operator fun times(v: Float) = this * v.toDouble()
//
//        operator fun times(v: Int) = this * v.toDouble()
//
//        operator fun div(v: Double) = this * (1.0 / v)
//
//        operator fun div(v: Float) = this / v.toDouble()
//
//        operator fun div(v: Int) = this / v.toDouble()
//    }
//
//    inner class chord(vararg notes: note) : Cloneable {
//        constructor(vararg chords: chord) : this(*chords.map { it.note_list }.merge().toTypedArray())
//        constructor(c: chord, vararg ns: note) : this(*(c.note_list.clone() + ns).toTypedArray())
//
//        val note_list = notes.toMutableList()
//        private val rest_note_list: MutableList<note> get() {
//            val tempList = mutableListOf<note>()
//            for (i in 1 until note_list.size)
//                tempList += note_list[i]
//            return tempList
//        }
//
//        val main: note get() {
//            return getChordNotesFromList()[0]
//            // note_list[0]
//        }
//
//        val second: note get() = note_list[1]
//        val init_duration: Double = main.duration
//
//        init {
//            if (note_list.size < 2) throw Exception("a chord require at least 2 notes")
//            for (i in 1 until note_list.size) {
//                note_list[i].duration = getRealDuration(.0)
//            }
//            //note_list[1].duration = getRealDuration(.0)
//        }
//
//        operator fun get(index: Int): note = getChordNotesFromList()[index]
//
//        operator fun plus(v: I): chord {
//            current.duration = getRealDuration(.0)
//            note_list.add(current)
//            return this
//        }
//
//        operator fun plus(pitch: Int): chord {
//            if (pitch != 0) {
//                getChordNotesFromList().forEach {
//                    it.pitch = (it.pitch + pitch).toByte()
//                }
//
//                note_list.forEach {
//                    it.pitch = (it.pitch + pitch).toByte()
//                }
//            }
//            return this
//        }
//
//        operator fun minus(pitch: Byte): chord = this + -pitch
//
//        operator fun plus(c: chord): chord {
//            val c2index = list.size - c.note_list.size
//            list[c2index].duration = getRealDuration(.0)
//            return chord(this, c)
//        }
//
//        operator fun times(v: Double): chord {
//            main.duration = getRealDuration(v)
//            note_list[0].duration = getRealDuration(v)
//            return this
//        }
//
//        operator fun times(v: Float): chord = this * v.toDouble()
//
//        operator fun times(v: Int): chord = this * v.toDouble()
//
//        operator fun div(v: Double): chord = this * (1.0 / v)
//
//        operator fun div(v: Float): chord = this / v.toDouble()
//
//        operator fun div(v: Int): chord = this / v.toDouble()
//
//        private fun getChordNotesFromList(): List<note> {
//            return list.filterIndexed { index, _ ->
//                index >= list.size - note_list.size
//            }
//        }
//
//        operator fun div(v: I): chord {
//            val bass = current.code
//            val pList = pop()
//            val chord_codes = getChordNotesFromList()
//            val notes_codes = chord_codes.map { it.code }
//            if (bass !in notes_codes) throw Exception("given note: ${pList[0].name} not in chord: $this")
//
//            chord_codes.forEach {
//                if (it.code < bass) {
//                    it.pitch++
//                }
//            }
//            return this
//        }
//
//        val sus4: chord get() {
//            val chord_notes = getChordNotesFromList()
//            val second = chord_notes[1]
//            when(second.code - main.code) {
//                4 -> second.code ++
//                3 -> second.code = (second.code + 2).toByte()
//                else -> {
//                    throw Exception("this chord did not contain a three degree note from root")
//                }
//            }
//            return chord(*chord_notes.clone().toTypedArray())
//        }
//
//        val sus: chord get() = sus4
//
//        val sus2: chord get() {
//            val chord_notes = getChordNotesFromList()
//            val second = chord_notes[1]
//            when(second.code - main.code) {
//                4 -> second.code = (second.code - 2).toByte()
//                3 -> second.code--
//                else -> {
//                    throw Exception("this chord did not contain a three degree note from root")
//                }
//            }
//
//            return chord(*chord_notes.clone().toTypedArray())
//        }
//
//        operator fun getValue(nothing: Nothing?, property: KProperty<*>): chord {
//            entrustc[property.name]?.let { list += it.clone() } ?: throw Exception("id ${property.name} is miss match")
//            return this
//        }
//
//        infix fun into(id: String): chord {
//            entrustc[id] = note_list.clone()
////            list -= note_list.toSet()
//            pop(note_list.size)
//            return this
//        }
//
//        override fun toString(): String = "<$note_list>"
//    }
//
//    inner class I(val id: Byte) {
//
//        constructor(n: Char) : this(getNoteBasicOffset(n))
//
//        init {
//            // if (id !in byteArrayOf(0, 2, 4, 5, 7, 9, 11)); // do sth.?
//        }
//
//        operator fun getValue(nothing: Nothing?, property: KProperty<*>): I {
//            entrusti[property.name]?.let { push(it) } ?: throw Exception("id ${property.name} is miss match")
//            return this
//        }
//
//        // todo 获得关系小调
//        infix fun relative(block: MDSL.() -> Any) {
//            !block
//        }
//
//        // 获取同名小调
//        infix fun minor(block: MDSL.() -> Any) {
//            atMainKeySignature {
//
//            }
//        }
//
//        infix fun into(id: String): I {
//            entrusti[id] = current.clone()
//            pop()
//            return this
//        }
//
//        operator fun invoke(mf: Int): Pair<Ks, Int> {
//            val res = getKeySignatureFromN(current, mf)
//            pop()
//            return res
//        }
//
//        operator fun invoke(mf: Int = major, block: MDSL.() -> Any) {
//            val tothat = this(mf)
//            val originKeySignature = keySignature
//            keySignature?.let {
//                keySignature = tothat
//                (it to tothat) (block)
//            } ?: run {
//                keySignature = tothat
//                (C(major) to tothat)(block)
//            }
//            keySignature = originKeySignature
//        }
//
//        operator fun get(pitch: Byte, duration: Double = this@MDSL.duration) : I {
//            if (pitch != 4.toByte()) {
//                current.pitch = pitch
//            }
//            current.duration = getRealDuration(duration)
//            return this
//        }
//
//        operator fun get(pitch: Byte, duration: Int): I = this[pitch, duration.toDouble()]
//
//        operator fun get(pitch: Byte, duration: Float): I = this[pitch, duration.toDouble()]
//
//        override fun toString(): String = "大弦嘈嘈如急雨，小弦切切如私语。嘈嘈切切错杂弹，大珠小珠落玉盘。"
//
//        operator fun not(): I {
//            current.sfn = SFNType.Natural
//            return this
//        }
//
//        infix fun up(v: Int): I {
//            current += v.toByte()
//            return this
//        }
//
//        infix fun down(v: Int): I {
//            current -= v.toByte()
//            return this
//        }
//
//        val dot: I get() = this dot 1
//
//        infix fun dot(v: Int): I {
//            current.duration *= Math.pow(1.5, v.toDouble())
//            return this
//        }
//
//        // 构建三和弦
//        infix fun triad(mode: Array<Int>): chord {
//            // todo 根据调性
//            return atMainKeySignature {
//                val second = current.clone()
//                second.duration = getRealDuration(.0)
//                second += mode[0].toByte()
//                val third = second.clone()
//                third += mode[1].toByte()
//                val chord = chord(current, second, third)
//                push(second.clone())
//                push(third.clone())
//                chord
//            } as chord
//        }
//
//        // 构建七和弦
//        infix fun seventh(mode: Array<Int>): chord {
//            val tr = T triad mode
//            return atMainKeySignature {
//                val fourth = tr.note_list[2].clone()
//                fourth += mode[2].toByte()
//                push(fourth.clone())
//                chord(tr, fourth)
//            } as chord
//        }
//
//        val addNinth: chord get() {
//            val tr = T triad majorChord
//            return atMainKeySignature {
//                val fourth = tr.note_list[0].clone()
//                fourth.duration = getRealDuration(.0)
//                fourth += 14.toByte()
//                push(fourth.clone())
//                chord(tr, fourth)
//            } as chord
//        }
//
//        val add9: chord get() = addNinth
//
//        // 构建九和弦
//        infix fun ninths(mode: Array<Int>): chord {
//            val tr = T seventh mode
//            return atMainKeySignature {
//                val fifth = tr.note_list[3].clone()
//                fifth += mode[3].toByte()
//                push(fifth.clone())
//                chord(tr, fifth)
//            } as chord
//        }
//
//
//        operator fun rangeTo(x: I) : Iin {
//            val lastIndex = list.lastIndex
//
//            val from = last
//            val to = current
//
//            if (from.code > to.code) throw Exception("from.code has to > to.code")
//
//            pop(2)
//
//            for (i in from.code..to.code) {
//                push(fromNoteId(i.toByte()))
//            }
//
//            return Iin(lastIndex, list.lastIndex, from, to)
//        }
//
//        operator fun unaryPlus() : I {
//            current.sfn = SFNType.Sharp
//            return this
//        }
//
//        operator fun unaryMinus(): I {
//            current.sfn = SFNType.Flat
//            return this
//        }
//
//        operator fun plus(x: Byte) : I {
//            val origin = current.pitch
//            current.pitch = (x + origin).toByte()
//            return this
//        }
//
//        operator fun plus(x: I): chord = chord(last, current)
//
//        operator fun plusAssign(x: Byte) {
//            current += x
//        }
//
//        operator fun minus(x: Byte) : I {
//            val origin = current.pitch
//            current.pitch = (origin - x).toByte()
//            return this
//        }
//
//        operator fun minusAssign(x: Byte) {
//            current -= x
//        }
//
//        operator fun times(x: Double) : I {
//            current.duration = getRealDuration(x)
//            return this
//        }
//
//        operator fun times(x: Int) : I = this * x.toDouble()
//
//        operator fun times(x: Float) : I = this * x.toDouble()
//
//        operator fun div(x: Double) : I = this * (1.0 / x)
//
//        operator fun div(x: Float) : I = this / x.toDouble()
//
//        operator fun div(x: Int) : I = this / x.toDouble()
//
//        infix fun C(x: I): I {
//            insert(list.lastIndex, 'C')
//            return i[0]
//        }
//
//        infix fun D(x: I): I {
//            insert(list.lastIndex, 'D')
//            return i[1]
//        }
//
//        infix fun E(x: I): I {
//            insert(list.lastIndex, 'E')
//            return i[2]
//        }
//
//        infix fun F(x: I): I {
//            insert(list.lastIndex, 'F')
//            return i[3]
//        }
//
//        infix fun G(x: I): I {
//            insert(list.lastIndex, 'G')
//            return i[4]
//        }
//
//        infix fun A(x: I): I {
//            insert(list.lastIndex, 'A')
//            return i[5]
//        }
//
//        infix fun B(x: I): I {
//            insert(list.lastIndex, 'B')
//            return i[6]
//        }
//
//    }
//}