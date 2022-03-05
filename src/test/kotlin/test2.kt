import whiter.music.mider.Mider
import whiter.music.mider.Note
import java.time.Duration
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

        (3 to 2.5) {

        }

        A {

        }

        (+B) {

        }

//        "Cm" {
//
//        }

        velocity(78) {

        }

        repeat(2) {
            F
        }

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
            push("C")
            println("get C")
            return i[0]
        }
    val D: I
        get() {
            push("D")
            println("get D")
            return i[1]
        }
    val E : I
        get() {
            push("E")
            println("get E")
            return i[2]
        }
    val F : I
        get() {
            push("F")
            println("get F")
            return i[3]
        }
    val G : I
        get() {
            push("G")
            println("get G")
            return i[4]
        }
    val A : I
        get() {
            push("A")
            println("get F")
            return i[5]
        }
    val B : I
        get() {
            push("B")
            println("get B")
            return i[6]
        }

    infix fun I.C(x: I): I {
        val index = list.size - 1
        insert(index, "C")
        println("invoke C")
        return i[0]
    }

    infix fun I.D(x: I): I {
        val index = list.size - 1
        insert(index, "D")
        println("invoke D")
        return i[1]
    }

    infix fun I.E(x: I): I {
        val index = list.size - 1
        insert(index, "E")
        println("invoke E")
        return i[2]
    }

    infix fun I.F(x: I): I {
        val index = list.size - 1
        insert(index, "F")
        println("invoke F")
        return i[3]
    }
    infix fun I.G(x: I): I {
        val index = list.size - 1
        insert(index, "G")
        println("invoke G")
        return i[4]
    }
    infix fun I.A(x: I): I {
        val index = list.size - 1
        insert(index, "A")
        println("invoke A")
        return i[5]
    }
    infix fun I.B(x: I): I {
        val index = list.size - 1
        insert(index, "B")
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

    operator fun Int.invoke(block: UI.() -> Unit) {
        val __pitch = _pitch
        _pitch = this.toByte()
        block()
        _pitch = __pitch
    }

    operator fun Double.invoke(block: UI.() -> Unit) {
        val __duration = _duration
        _duration = this
        block()
        _duration = __duration
    }

    operator fun Pair<Int, Double>.invoke(block: UI.() -> Unit) {
        val __pitch = _pitch
        _pitch = this.first.toByte()

        val __duration = _duration
        _duration = this.second

        block()

        _pitch = __pitch
        _duration = __duration
    }

    operator fun I.invoke(arg: String = "", block: UI.() -> Unit) {
        pop()
        block()
        TODO("imp: 调号")
    }

    operator fun I.not(): I {
        // todo 还原记号 natural
        return this
    }

    fun repeat(times: Int, block: UI.() -> Unit) {
        if (times == 0) return
        for (i in 0 until times) block()
    }

    fun velocity(v: Byte, block: UI.() -> Unit) {
        val __velocity = _velocity
        _velocity = v
        block()
        _velocity = __velocity
    }

    private fun push(x: String) {
        list.add(O(x, _pitch, _duration))
    }

    private fun pop() {
        list.removeLast()
    }

    private fun insert(index: Int, x: String) {
        list.add(index, O(x, _pitch, _duration))
    }

    class O(var note: String, var pitch: Byte = 4, var duration: Double = 1.0, var velocity: Byte = 100) {
        override fun toString(): String {
            return "($note, $pitch, $duration, $velocity)"
        }
    }

    class Iin(var first: Int, var last: Int) {

    }

    inner class I(val id: Byte) {
        operator fun get(duration: Double, pitch: Byte = 4) : I {
            if (pitch != 4.toByte()) {
                val origin = list[list.lastIndex].pitch
                list[list.lastIndex].pitch = (pitch + origin).toByte()
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