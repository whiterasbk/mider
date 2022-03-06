import kotlin.reflect.KFunction
import kotlin.reflect.KProperty


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

        5 {
            "A+6:!D+1:+A[.25]:C A"
        }

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
    val list = mutableListOf<O>()
    val i = listOf(I(0), I(1), I(2), I(3), I(4), I(5), I(6))
    val key_signature_keys = null
    var _pitch: Byte = 4
    var _duration = 1.0
    var _velocity: Byte = 100

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
            println("get F")
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
        list[list.lastIndex].duration = .0
        return this
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
        list[list.lastIndex].duration = x
        println("$this plus $x")
        return this
    }

    operator fun I.times(x: Float) : I {
        return this * x.toDouble()
    }

    operator fun I.times(x: Int) : I {
        return this * x.toDouble()
    }

    operator fun Int.invoke(block: UI.() -> Any) {
        val __pitch = _pitch
        _pitch = this.toByte()
        val res = block()
        if (res is String) parse(res)
        _pitch = __pitch
    }

    operator fun Double.invoke(block: UI.() -> Any) {
        val __duration = _duration
        _duration = this
        val res = block()
        if (res is String) parse(res)
        _duration = __duration
    }

    operator fun Pair<Int, Double>.invoke(block: UI.() -> Any) {
        val __pitch = _pitch
        _pitch = this.first.toByte()

        val __duration = _duration
        _duration = this.second

        val res = block()
        if (res is String) parse(res)

        _pitch = __pitch
        _duration = __duration
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

            if (length == 1) {
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
                    } else if ('+' in withoutsnf || '*' in withoutsnf || '-' in withoutsnf) {
                        val d = if ('*' in withoutsnf) (withoutsnf.split('*')[1].split('+')[0].split('-')[0]).toDouble() else iDuration
                        val p = if ('+' in withoutsnf)
                            (withoutsnf.split('+')[1].split('*')[0]).toInt().toByte()
                        else if ('-' in withoutsnf) (-(withoutsnf.split('-')[1].split('*')[0]).toInt()).toByte() else 0

                        push(noteName, (_pitch + p).toByte(), d, snf)
                    } else throw Exception("parse failed")
                }

            }

        }
    }

    private fun push(x: Char, pitch: Byte = _pitch, duration: Double = _duration, sfn: SFNType = SFNType.Self) {
        list.add(O(x, pitch, duration, _velocity, sfn))
    }

    private fun pop() {
        list.removeLast()
    }

    private fun insert(index: Int, x: Char, pitch: Byte = _pitch, duration: Double = _duration, sfn: SFNType = SFNType.Self) {
        list.add(index, O(x, pitch, duration, _velocity, sfn))
    }

    class O(var note: Char, var pitch: Byte = 4, var duration: Double = 1.0, var velocity: Byte = 100, var sfn: SFNType = SFNType.Self) {

        init {
            if (note !in "CDEFGAB") throw Exception("unsupport note: $note")
            if (duration < 0) throw Exception("duration:$duration has to > 0")
        }

        override fun toString(): String {
            return "(${when(sfn){
                SFNType.Sharp -> '#'
                SFNType.Flat -> 'b'
                SFNType.Natural -> '&'
                else -> ""
            }}$note$pitch*${duration}v$velocity)"
        }
    }

    enum class SFNType {
        Flat, Sharp, Natural, Self
    }

    class Iin(var first: Int, var last: Int) {

    }

    inner class I(val id: Byte) {
        operator fun get(duration: Double, pitch: Byte = 4) : I {
            if (pitch != 4.toByte()) {
                list[list.lastIndex].pitch = pitch
            }
            list[list.lastIndex].duration = duration
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