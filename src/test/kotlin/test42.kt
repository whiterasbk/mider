import whiter.music.mider.*
import kotlin.math.*


fun main(args: Array<String>) {
    test2()


}


fun test2() {
    val mdsl = MDSL()
    var minimsTicks = 960
//    minimsTicks = 839
    val clock: Byte = 18

    with(mdsl) {
//        timeSignature = 4 to 4
//        keySignature(F, major)
        program = MDSL.instrument.violin
        bpm = 120
        keySignature(C, major)

//        val d by -D/2+2 into "d"
//        val ca by D into "ca"
//
//        d*4
//
        val Am by A+B+C into "Am"
        val Bm by A+D+C into "Bm"
        val Cm by C+E+G into "Cm"



//        val j by Cm.sus2[6]*5 into "j"
//
//        j
//        val c by C into "c"


//
//        E {
//             C..B under majorScale
//        }

//        val c = atMainKeySignature {
//            C D E
//        }
//
//        println(c)
//
//        D {
////            D C D
//            C {
////                C; D; E
//                val Fd by C ninths add into "Fd"
//                Fd
//        val p by C.add9 + 1 into "p"
//        p[0] += 4
//
//
//
//
////        println("="+p[0]+=0)
//
//        D seventh majorChord
//
//        D





        (C(major) to D(minor)) {
//            Am; C; E; G; A

            C..B under majorScale
        }

//        C..B under minorScale

//        (C to D) {
//            Bm
//        }





//        val a1 by D seventh decreasedSeventh into "a1"
//        a1
//            }
//        }
//
//        Am and Bm
//
//        Bm; Am

//        println(">>>"+entrustc)
//
//        val d = Am + Bm
//////
//////        println(d.note_list)
//////
//        val ab by d into "ab"
//////
//        val cc by ab + C into "cc"


//        cc
//
//        E; D
//
//        val Cd by C+E+G into "Cd"
//
//        Cd / E

//        A + B + C

//        val Cm by C+E+G into "Cm"
//        val V by Cm into "V"
//        V
//        Cm.sus4
//        V
//
//        Am; Cm


//        Am + Bm
//        Am










//        A triad majorChord

//
//        Fd


//        println(A)
//
//        D relative {
//
//        }


        list.forEach(::println)

//        println("========")
//        Am.note_list.forEach(::println)
//        println("========")
//        Bm.note_list.forEach(::println)

//        (C to D) {
//
//        }




//        A B C D D
//
//        C..E under diatonic
//        D[4]
//        !D + 1
//        (C to A) {
           // G; E*2+1; E+1; G; D*2+1; D+1; G; C*2+1; C*2+1; C*2+1; D*2+1

//        }
    }

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

    midi.save("src/main/resources/mdsl.mid")
}

fun a() {

    with(MDSL()) {

        keySignature(D, major)

        (+D)(major) {
            C; D; E
        }

        (F)(major) {
            C; D; E
        }

        println(signatureKeysList)


//        (C..G) step 1// major
//        (C to D) {
//            C D E F G A B
//        }


//        var n = arrayOf(2,2,1,2,2,2,1)
//        var c = 0
//        for (i in 0..32) {
//            var p = 0
//            p+=n[c % n.size]
//            println("$p, ${n[c % n.size]}")
//            c++
//        }

//        var from = 24
//        var to = 35
//        var i = from
//        var loopc = 0
//
//        while (i <= to) {
//            println(i)
//            i += n[loopc%n.size]
//            loopc++
//        }
        list.forEach {
            println(it)
        }
    }
}