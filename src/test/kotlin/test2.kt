fun main(args: Array<String>) {

    with(UI()) {
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

        '4' {
            A; B; O/2; C; C/2+1; C*4; C[1,2]; O
            "C O/2 B O*4"
        }



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

class UI {
    val list = mutableListOf<note>()
    val i = listOf(I(0), I(1), I(2), I(3), I(4), I(5), I(6))
    val __rest_instance = rest()
    val key_signature_keys = null
    var _pitch: Byte = 4
    var _duration = 1.0 // 1.0为全音符
    var defaultNoteDuration = 4 // 默认是四分音符
    var _velocity: Byte = 100

    /*
     * 开头不能是休止符
     */
    val O: rest
        get() {
            if (list.size == 0) throw Exception("rest note should not place at the beginning")
            list[list.lastIndex].duration += getRealDuration(_duration)
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

    infix fun I.C(x: I): I {
        val index = list.size - 1
        insert(index, 'C')
        println("invoke C")
        return i[0]
    }
    infix fun I.D(x: I): I {
        val index = list.size - 1
        insert(index, 'D')
        println("invoke D")
        return i[1]
    }
    infix fun I.E(x: I): I {
        val index = list.size - 1
        insert(index, 'E')
        println("invoke E")
        return i[2]
    }
    infix fun I.F(x: I): I {
        val index = list.size - 1
        insert(index, 'F')
        println("invoke F")
        return i[3]
    }
    infix fun I.G(x: I): I {
        val index = list.size - 1
        insert(index, 'G')
        println("invoke G")
        return i[4]
    }
    infix fun I.A(x: I): I {
        val index = list.size - 1
        insert(index, 'A')
        println("invoke A")
        return i[5]
    }
    infix fun I.B(x: I): I {
        val index = list.size - 1
        insert(index, 'B')
        println("invoke B")
        return i[6]
    }

    infix fun I.and(v: I): I {
        list[list.lastIndex].duration = getRealDuration(.0)
        return this
    }

    infix fun I.up(v: Int): I {
        TODO("升高半音")
    }

    infix fun I.down(v: Int): I {
        TODO("降低半音")
    }

    infix fun Iin.step(v: Byte) {
        TODO("imp")
    }

    operator fun I.rangeTo(x: I) : Iin {
        TODO("imp")
        x.id - this.id
        return Iin(0, 0)
    }

    operator fun I.unaryPlus() : I {
        TODO("imp")
        println("+$this")
        return this
    }

    operator fun I.unaryMinus(): I {
        TODO("imp")
        println("-$this")
        return this
    }

    operator fun I.plus(x: Byte) : I {
        val origin = list[list.lastIndex].pitch
        list[list.lastIndex].pitch = (x + origin).toByte()
        println("$this plus $x")
        return this
    }

    operator fun I.minus(x: Byte) : I {
        val origin = list[list.lastIndex].pitch
        list[list.lastIndex].pitch = (origin - x).toByte()
        println("$this minus $x")
        return this
    }

    operator fun I.times(x: Double) : I {
        list[list.lastIndex].duration = getRealDuration(x)
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
        list[list.lastIndex].duration -= getRealDuration(_duration)
        list[list.lastIndex].duration += getRealDuration(_duration) * v
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

    operator fun Int.invoke(block: UI.() -> Any) {
        val __pitch = _pitch
        _pitch = this.toByte()
        val res = block()
        if (res is String) parse(res)
        _pitch = __pitch
    }

    // 设定任意时值
    operator fun Double.invoke(block: UI.() -> Any) {
        val _dnd = defaultNoteDuration
        defaultNoteDuration = 1
        val __duration = _duration
        _duration = this
        val res = block()
        if (res is String) parse(res)
        _duration = __duration
        defaultNoteDuration = _dnd
    }

    // 设置默认音符时值, 这样可以避免出现小数（
    operator fun Char.invoke(block: UI.() -> Any) {
        if (this !in "123456789") throw Exception("can not set default note duration to $this, it should in 1-9")
        val _dnd = defaultNoteDuration
        defaultNoteDuration = this.digitToInt()
        val res = block()
        if (res is String) parse(res)
        defaultNoteDuration = _dnd
    }

    // 设定音高和任意时值
    operator fun Pair<Int, Double>.invoke(block: UI.() -> Any) {
        val __pitch = _pitch
        _pitch = this.first.toByte()

        val __duration = _duration
        _duration = this.second

        val _dnd = defaultNoteDuration
        defaultNoteDuration = 1

        val res = block()
        if (res is String) parse(res)

        _pitch = __pitch
        _duration = __duration
        defaultNoteDuration = _dnd
    }

    // 其实只是想避免出现小数（
    @JvmName("invokeIntChar")
    operator fun Pair<Int, Char>.invoke(block: UI.() -> Any) {
        val __pitch = _pitch
        _pitch = this.first.toByte()

        val _dnd = defaultNoteDuration
        defaultNoteDuration = this.second.digitToInt()

        val res = block()
        if (res is String) parse(res)

        _pitch = __pitch
        defaultNoteDuration = _dnd
    }

    operator fun I.invoke(arg: String = "", block: UI.() -> Any) {
        pop()
        val res = block()
        if (res is String) parse(res)
        TODO("imp: 调号")
    }

    operator fun I.not(): I {
        // todo 还原记号 natural
        return this
    }

    operator fun String.not() {
        parse(this)
    }

    fun repeat(times: Int, block: UI.() -> Any) {
        if (times == 0) return
        for (i in 0 until times) {
            val res = block()
            if (res is String) parse(res)
        }
    }

    fun velocity(v: Byte, block: UI.() -> Any) {
        val __velocity = _velocity
        _velocity = v
        val res = block()
        if (res is String) parse(res)
        _velocity = __velocity
    }

    private fun parse(str: String, isChord: Boolean = false) {
        str.trim().replace("\n", " ").split(" ").forEach {
            val length = it.length
            val first_letter = it[0]
            val iDuration = if (isChord) 0.0 else _duration

            if (first_letter == 'O') {
                // rest
                if (list.size == 0) throw Exception("rest note should not place at the beginning")

                if (length == 1) {
                    list[list.lastIndex].duration += getRealDuration(_duration)
                } else if (it[1] == '*') {
                    val v = it.substring(2 until it.length).toDouble()
                    list[list.lastIndex].duration += getRealDuration(_duration) * v
                } else if (it[1] == '/') {
                    val v = it.substring(2 until it.length).toDouble()
                    list[list.lastIndex].duration += getRealDuration(_duration) * (1.0 / v)
                } else throw Exception("parse failed")
            } else if (length == 1) {
                push(it[0], duration = iDuration)
            } else if (length == 2) {
                push(it[1], duration = iDuration, sfn = when(first_letter) {
                    '-' -> SFNType.Flat
                    '+' -> SFNType.Sharp
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

                    if (first_letter in "-+!") {
                        snf = when(first_letter) {
                            '-' -> SFNType.Flat
                            '+' -> SFNType.Sharp
                            '!' -> SFNType.Natural
                            else -> SFNType.Self
                        }
                        withoutsnf = it.substring(1 until length)
                    }

                    val noteName = withoutsnf[0]

                    if ('[' in withoutsnf) {
                        val args = withoutsnf.substring(1 until  withoutsnf.length).replace("[", "").replace("]", "").split(',')
                        if (args.size == 1) {
                            push(noteName, _pitch, args[0].toDouble(), snf)
                        } else if (args.size == 2) {
                            push(noteName, args[1].toInt().toByte(), args[0].toDouble(), snf)
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

    private fun pop() {
        list.removeLast()
    }

    private fun getRealDuration(d: Double): Double {
        if (defaultNoteDuration == 1) return d
        if (d < 0) throw Exception("what if duration can be negative? anyway, now the duration of note should be positive")
        return d * (1.0 / defaultNoteDuration)
    }

    private fun insert(index: Int, x: Char, pitch: Byte = _pitch, duration: Double = _duration, sfn: SFNType = SFNType.Self) {
        list.add(index, note(x, pitch, getRealDuration(duration), _velocity, sfn))
    }

    class note(var name: Char, var pitch: Byte = 4, var duration: Double = 1.0, var velocity: Byte = 100, var sfn: SFNType = SFNType.Self) {

        init {
            if (name !in "CDEFGAB") throw Exception("unsupport note: $name")
            if (duration < 0) throw Exception("duration:$duration has to > 0")
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

    enum class SFNType {
        Flat, Sharp, Natural, Self
    }

    class Iin(var first: Int, var last: Int) {

    }

    inner class rest

    inner class I(val id: Byte) {
        operator fun get(duration: Double, pitch: Byte = 4) : I {
            if (pitch != 4.toByte()) {
                list[list.lastIndex].pitch = pitch
            }
            list[list.lastIndex].duration = getRealDuration(duration)
            return this
        }

        operator fun get(duration: Int, pitch: Byte = 4) : I {
            return this[duration.toDouble(), pitch]
        }

        operator fun get(duration: Float, pitch: Byte = 4) : I {
            return this[duration.toDouble(), pitch]
        }

        override fun toString(): String {
            return hashCode().toString()
        }
    }
}