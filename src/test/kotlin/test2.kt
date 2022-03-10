import whiter.music.mider.Note

fun main(args: Array<String>) {

    with(MDSL()) {
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
        list.forEach {
            println(it)
        }
    }
}

class MDSL {
    val list = mutableListOf<note>()
    private val i = listOf(I(0), I(2), I(4), I(5), I(7), I(9), I(11))
    private val __rest_instance = rest()
    var _pitch: Byte = 4
    var _duration = 1.0 // 1.0为全音符
    var defaultNoteDuration = 4 // 默认是四分音符
    var _velocity: Byte = 100
    val major = 0
    val minor = 1
    var diatonic = arrayOf(2, 2, 1, 2, 2, 2, 1)
    var program: instrument = instrument.piano
    var bpm = 80
    var timeSignature = 4 to 4
    val signatureKeysList = mutableListOf<Pair<Pair<Ks, Int>, IntRange>>()
    var keySignature: Pair<Ks, Int> = getKeySignatureFromN(note('C'), major)

    private val current: note
        get() = list[list.lastIndex]
    val end = 0

    /*
     * 开头不能是休止符
     */
    val O: rest
        get() {
            if (list.size == 0) throw Exception("rest note should not place at the beginning")
            current.duration += getRealDuration(_duration)
            return __rest_instance
        }
    val C: I 
        get() {
            push('C')
            println("get C")
            return i[0]
        }
    val D: I
        get() {
            push('D')
            println("get D")
            return i[1]
        }
    val E : I
        get() {
            push('E')
            println("get E")
            return i[2]
        }
    val F : I
        get() {
            push('F')
            println("get F")
            return i[3]
        }
    val G : I
        get() {
            push('G')
            println("get G")
            return i[4]
        }
    val A : I
        get() {
            push('A')
            println("get A")
            return i[5]
        }
    val B : I
        get() {
            push('B')
            println("get B")
            return i[6]
        }


    infix fun I.and(v: I): I {
        current.duration = getRealDuration(.0)
        return this
    }

    infix fun I.up(v: Int): I {
        current += v.toByte()
        return this
    }

    infix fun I.down(v: Int): I {
        current -= v.toByte()
        return this
    }

    infix fun I.dot(v: Int): I {
        current.duration *= Math.pow(1.5, v.toDouble())
        return this
    }

    infix fun Iin.step(v: Byte) {
        clear(list)
        for (i in from.code until (to.code) step v.toInt()) {
            push(fromNoteId(i.toByte()))
        }
    }

    infix fun Iin.under(v: Array<Int>) {
        clear(list)
        var i = from.code.toInt()
        var loopc = 0
        while (i <= to.code) {
            push(fromNoteId(i.toByte()))
            i += v[loopc%v.size]
            loopc++
        }
    }

    operator fun I.rangeTo(x: I) : Iin {
        val lastIndex = list.lastIndex
        val from = list[list.lastIndex - 1]
        val to = current

        if (from.code > to.code) throw Exception("from.code has to > to.code")

        pop(2)

        for (i in from.code..to.code) {
            push(fromNoteId(i.toByte()))
        }

        return Iin(lastIndex, list.lastIndex, from, to)
    }

    operator fun I.unaryPlus() : I {
        current.sfn = SFNType.Sharp
        return this
    }

    operator fun I.unaryMinus(): I {
        current.sfn = SFNType.Flat
        return this
    }

    operator fun I.plus(x: Byte) : I {
        val origin = current.pitch
        current.pitch = (x + origin).toByte()
        println("$this plus $x")
        return this
    }

    operator fun I.plusAssign(x: Byte) {
        current += x
    }

    operator fun I.minus(x: Byte) : I {
        val origin = current.pitch
        current.pitch = (origin - x).toByte()
        println("$this minus $x")
        return this
    }

    operator fun I.minusAssign(x: Byte) {
        current -= x
    }

    operator fun I.times(x: Double) : I {
        current.duration = getRealDuration(x)
        println("$this plus $x")
        return this
    }

    operator fun I.div(x: Double) : I {
        return this * (1.0 / x)
    }

    operator fun I.times(x: Float) : I {
        return this * x.toDouble()
    }

    operator fun I.div(x: Float) : I {
        return this * (1.0 / x)
    }

    operator fun I.times(x: Int) : I {
        return this * x.toDouble()
    }

    operator fun I.div(x: Int) : I {
        return this * (1.0 / x)
    }

    operator fun rest.times(v: Double) {
        current.duration -= getRealDuration(_duration)
        current.duration += getRealDuration(_duration) * v
    }

    operator fun rest.times(v: Float) {
        this * v.toDouble()
    }

    operator fun rest.times(v: Int) {
        this * v.toDouble()
    }

    operator fun rest.div(v: Double) {
        this * (1.0 / v)
    }

    operator fun rest.div(v: Float) {
        this * (1.0 / v)
    }

    operator fun rest.div(v: Int) {
        this * (1.0 / v)
    }

    operator fun Int.invoke(block: MDSL.() -> Any) {
        val __pitch = _pitch
        _pitch = this.toByte()
        !block
        _pitch = __pitch
    }

    // 设定任意时值
    operator fun Double.invoke(block: MDSL.() -> Any) {
        '1' {
            val __duration = _duration
            _duration = this@invoke
            !block
            _duration = __duration
            end
        }
    }

    // 设置默认音符时值, 这样可以避免出现小数（
    operator fun Char.invoke(block: MDSL.() -> Any) {
        if (this !in "123456789") throw Exception("can not set default note duration to $this, it should in 1-9")
        val _dnd = defaultNoteDuration
        defaultNoteDuration = this.digitToInt()
        !block
        defaultNoteDuration = _dnd
    }

    // 设定音高和任意时值
    operator fun Pair<Int, Double>.invoke(block: MDSL.() -> Any) {

        this.first {
            this@invoke.second {
                '1' {
                    !block
                }
            }
        }
    }

    // 其实只是想避免出现小数（
    @JvmName("invokeIntChar")
    operator fun Pair<Int, Char>.invoke(block: MDSL.() -> Any) {
        this.first {
            this@invoke.second {
                !block
            }
        }
    }

    // todo 标记调号
    operator fun I.invoke(mf: Int = 0, block: MDSL.() -> Any) {
        val sfmf = getKeySignatureFromN(current, mf)
        pop()
        val smark = list.size
        !block
        val emark = list.size
        signatureKeysList.add(sfmf to (smark until emark))
    }

    @JvmName("invokeII")
    operator fun Pair<I, I>.invoke(block: MDSL.() -> Any){
        val dp = current.code - list[list.lastIndex - 1].code  // this.second.id - this.first.id
        pop(2)

        val smark = list.size
        !block
        val emark = list.size
        if (smark == emark) return

        // todo 小调
        signatureKeysList.add(getKeySignatureFromN(current, major) to (smark until emark))

        for (i in smark until emark) {
            if (list[i].sfn != SFNType.Natural) {
                list[i] += dp.toByte()
                // todo 是否要升高八度
//                list[i].pitch = (list[i].pitch + if (dp > 0) 1 else 0).toByte()
            }
        }

    }

    fun repeat(times: Int, block: MDSL.() -> Any) {
        if (times <= 0) return
        for (i in 0 until times) !block
    }

    fun def(block: MDSL.() -> Any): MDSL.() -> Any {
        return block
    }

    //    operator fun I.not(): I {
//        current.sfn = SFNType.Natural
//        return this
//    }

    operator fun String.not() {
        parse(this)
    }

    operator fun (MDSL.() -> Any).not() {
        val res = this()
        if (res is String) parse(res)
    }

    fun velocity(v: Byte, block: MDSL.() -> Any) {
        val __velocity = _velocity
        _velocity = v
        !block
        _velocity = __velocity
    }

    // 标记默认调号
    fun keySignature(v: I, s: Int) {
        keySignature = getKeySignatureFromN(current, s)
        pop()
    }

    private fun parse(str: String, isChord: Boolean = false) {
        str.trim().replace("\n", " ").replace(";", " ").replace("  ", " ").split(" ").forEach {
            val length = it.length
            val first_letter = it[0]
            val iDuration = if (isChord) 0.0 else _duration

            if (first_letter == 'O') {
                // rest
                if (list.size == 0) throw Exception("rest note should not place at the beginning")

                if (length == 1) {
                    current.duration += getRealDuration(_duration)
                } else if (it[1] == '*') {
                    val v = it.substring(2 until it.length).toDouble()
                    current.duration += getRealDuration(_duration) * v
                } else if (it[1] == '/') {
                    val v = it.substring(2 until it.length).toDouble()
                    current.duration += getRealDuration(_duration) * (1.0 / v)
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
                            push(noteName, args[0].toInt().toByte(), getRealDuration(_duration), snf)
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

                        push(noteName, (_pitch + p).toByte(), d, snf)
                    } else throw Exception("parse failed")
                }

            }

        }
    }

    private fun push(x: Char, pitch: Byte = _pitch, duration: Double = _duration, sfn: SFNType = SFNType.Self) {
        list.add(note(x, pitch, getRealDuration(duration), _velocity, sfn))
    }

    private fun push(n: note) {
        list.add(n)
    }

    private fun pop(n: Int = 1) {
        for (i in 0 until n) list.removeLast()
    }

    private fun getRealDuration(d: Double): Double {
        if (defaultNoteDuration == 1) return d
        if (d < 0) throw Exception("what if duration can be negative? anyway, now the duration of note should be positive")
        return d * (1.0 / defaultNoteDuration)
    }

    private fun insert(index: Int, x: Char, pitch: Byte = _pitch, duration: Double = _duration, sfn: SFNType = SFNType.Self) {
        list.add(index, note(x, pitch, getRealDuration(duration), _velocity, sfn))
    }

    private fun insert(index: Int, n: note) {
        list.add(index, n)
    }

    private fun fromNoteId(i: Byte): note {
        val n = note('C')
        n.code = i
        return n
    }

    companion object {
        private fun nextNoteName(s: Char): Char {
            return when(s) {
                'C' -> 'D'
                'D' -> 'E'
                'E' -> 'F'
                'F' -> 'G'
                'G' -> 'A'
                'A' -> 'B'
                'B' -> 'C'
                else -> throw Exception("$s not in CDEFGAB")
            }
        }

        private fun previousNoteName(s: Char): Char {
            return when(s) {
                'C' -> 'B'
                'D' -> 'C'
                'E' -> 'D'
                'F' -> 'E'
                'G' -> 'F'
                'A' -> 'G'
                'B' -> 'A'
                else -> throw Exception("$s not in CDEFGAB")
            }
        }

        fun getKeySignatureFromN(n: note, s: Int): Pair<Ks, Int> {
//            val sf = when(n.name) {
//                'C' -> {
//                    if (n.sfn == SFNType.Sharp) 7 else 0
//                }
//                'D' -> {
//                    if (n.sfn == SFNType.Flat) -5 else 2
//                }
//                'E' -> {
//                    if (n.sfn == SFNType.Flat) -3 else 4
//                }
//                'F' -> {
//                    if (n.sfn == SFNType.Sharp) 6 else -1
//                }
//                'G' -> {
//                    if (n.sfn == SFNType.Flat) -6 else 1
//                }
//                'A' -> {
//                    if (n.sfn == SFNType.Flat) -4 else 3
//                }
//                'B' -> {
//                    if (n.sfn == SFNType.Flat) -2 else 5
//                }
//                else -> throw Exception("no such signature key")
//            }
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
    }

    // key signature
    enum class Ks(val semitone: Int) {
        C(0), G(1), D(2), A(3), E(4), B(5), `#F`(6),
        `#C`(7), F(-1), bB(-2), bE(-3), bA(-4), bD(-5), bG(-6), bC(-7)
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

    inner class note(var name: Char, var pitch: Byte = _pitch, var duration: Double = getRealDuration(_duration), var velocity: Byte = _velocity, var sfn: SFNType = SFNType.Self) {

        init {
            if (name !in "CDEFGAB") throw Exception("unsupport note: $name")
            if (duration < 0) throw Exception("duration:$duration has to > 0")
        }

        var code: Byte = 0
            get() {

                val relname = when(sfn) {
                    SFNType.Sharp -> {
                        if (name in "EB") {
                            nextNoteName(name)
                        } else {
                            name + "S"
                        }
                    }
                    SFNType.Flat -> {
                        previousNoteName(name)
                    }
                    else -> name
                }.toString()

                return Note.valueOf("$relname${
                    when (sfn){
                        SFNType.Flat -> {
                            if (relname !in "BE") 'S' else ""
                        }
                        else -> ""
                    }
                }${pitch - when(sfn) {
                    SFNType.Sharp -> {
                        if (name == 'B') -1 else 0
                    }
                    SFNType.Flat -> {
                        if (name == 'C') 1 else 0
                    }
                    else -> 0
                }}").id
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

        operator fun plusAssign(v: Byte) {
            code = (code + v).toByte()
        }

        operator fun minusAssign(v: Byte) {
            code = (code - v).toByte()
        }

        override fun toString(): String {
            return "(${when(sfn){
                SFNType.Sharp -> '#'
                SFNType.Flat -> 'b'
                SFNType.Natural -> '&'
                else -> ""
            }}$name${pitch}_${duration}_$velocity)"
        }
    }

    enum class SFNType(val symbol: Char) {
        Flat('#'), Sharp('S'), Natural('N'), Self(' ')
    }

    class Iin(val first: Int, val last: Int, val from: note, val to: note) {
        override fun toString(): String {
            return "first: $first; last: $last; from: $from; to: $to"
        }

        fun clear(l: MutableList<note>) {
            for (i in first..(last + 1)) {
                l.removeLast()
            }
        }
    }

    inner class rest

    inner class chord() {
        // todo 实现和弦
    }

    inner class I(val id: Byte) {

        constructor(n: Char) : this(when(n){
            'C' -> 0
            'D' -> 2
            'E' -> 4
            'F' -> 5
            'G' -> 7
            'A' -> 9
            'B' -> 11
            else -> throw Exception("no such note")
        })

        init {
            if (id !in byteArrayOf(0, 2, 4, 5, 7, 9, 11)); // do sth.?
        }

        operator fun get(pitch: Byte, duration: Double = _duration) : I {
            if (pitch != 4.toByte()) {
                current.pitch = pitch
            }
            current.duration = getRealDuration(duration)
            return this
        }

        operator fun get(pitch: Byte, duration: Int) : I {
            return this[pitch, duration.toDouble()]
        }

        operator fun get(pitch: Byte, duration: Float) : I {
            return this[pitch, duration.toDouble()]
        }

        override fun toString(): String {
            return Note.from(id).toString()
        }

        operator fun not(): I {
            current.sfn = SFNType.Natural
            return this
        }

        infix fun C(x: I): I {
            val index = list.size - 1
            insert(index, 'C')
            println("invoke C")
            return i[0]
        }

        infix fun D(x: I): I {
            val index = list.size - 1
            insert(index, 'D')
            println("invoke D")
            return i[1]
        }

        infix fun E(x: I): I {
            val index = list.size - 1
            insert(index, 'E')
            println("invoke E")
            return i[2]
        }

        infix fun F(x: I): I {
            val index = list.size - 1
            insert(index, 'F')
            println("invoke F")
            return i[3]
        }

        infix fun G(x: I): I {
            val index = list.size - 1
            insert(index, 'G')
            println("invoke G")
            return i[4]
        }

        infix fun A(x: I): I {
            val index = list.size - 1
            insert(index, 'A')
            println("invoke A")
            return i[5]
        }

        infix fun B(x: I): I {
            val index = list.size - 1
            insert(index, 'B')
            println("invoke B")
            return i[6]
        }
    }
}