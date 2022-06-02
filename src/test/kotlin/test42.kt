import whiter.music.mider.MidiFile
import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.dsl.apply

import whiter.music.mider.dsl.MiderDSL.*
import whiter.music.mider.dsl.play
import whiter.music.mider.dsl.playDslInstance
import javax.sound.midi.MidiSystem

fun main(args: Array<String>) {

//    val m = MidiFile().append {
//        track {
//            changeProgram(0)
//            for (i in 0..24)
//                note((64-i).toByte(), time = 800)
//        }
//
//        track {
//            changeProgram(41)
//            for (i in 0..24)
//                note((69 + i).toByte(), time = 900)
//        }
//
//    }.inStream()
//
//    val se = MidiSystem.getSequencer()
//    se.setSequence(m)
//    se.open()
//    se.start()

//


    play {
        !"A[2,3]"
    }

//    test2()

//    val list = mutableListOf(1)
//    list.add(list.lastIndex, 0)
//    println(list)

//    val l1 = listOf<Int>()
//    val l2 = listOf<Int>(5)
//    val l3 = (l1 + l2).toMutableList()
//    l2.reversed().forEachIndexed { index, it ->
//        l3.add(l3.size - index * 2, it)
//    }
//    println(l3)

//    app()

}

private fun app() {
    apply("saigetsu.mid") {
        bpm = 100

        C(minor) {
            repeat {
                G; B
                5 { C; E; F*2; E; F; G*2; E; C }
                B; G
                5 { E; F; C*2; C; E; F*2; E; F; G*2; B; C+1 }
                6 { E; D; C/4; D/4; C/2; B-1; C*2 }
                5 {
                    B; G; F*2; G; E; F*2; E; F; G.dot; C/2; E/2; F/2; E; C*2; C; B-1
                    C.dot; B/4-1; C/4; E; F; G; F; B*2-1; C*4; G-1; B-1; C; E
                }

                val P0 = exec { (F[5]-A) { A*2; G; A; B*2 } }

                5 { E; C; B-1; G-1; E; F; C*2; C; E }; !P0
                6 { B-1; C; E; D; C/4; D/4; C/2; B-1; C*2 }
                5 {
                    C+B; B-1+G; A*2-1+F; B-1+G; A-1+E; B*2-1+F; G-1+E; A-1+F
                    C.dot+G; C/2; E/2; F/2; E; C*2; C+E; B-1+F; C.dot+E; B/4-1; C/4
                    E; F; G; F; G*2+B; C*4+1+E
                }
                G; B
                5 {
                    C; F; B; C+1; G; F; G*2+B; E; F; D; E; D/4; E/4; D/2; B-1; F*2-1+C
                }
                G; B
                5 {
                    C; E; A-1+F; G-1+E; A.dot-1+F; E/2; F/2; G/2; E; D.dot+F; (B/2-1 or C/2) + E; D+F; B; E*2+G; G; B
                    C*2+1+A; B; C+1; G*2.dot; E; F; G; F/4; G/4; F/2; E; C*2
                    C; B-1; C.dot; B/4-1; C/4; E; F; G; F; B*2
                    replace({ C*4+1+E+G }, { C*16+1+E+G })
                }
            }
        }

        track {
            pitch = 2
            velocity = 60

            C(minor) {
//                repeat {
                    O*4
                    val head1 = def { A; A; E+1; A; B; B; F+1; B }
                    val tailPrefix = def { higher { C; C; G; C } }
                    val tailSuffix = def { higher { E; D; C; B-1 } }

                    val tail1 = def { !tailPrefix; !tailSuffix }
                    val tail2 = def { !tailPrefix; !tailPrefix }

                    val melody1 = exec { !head1; !tail1 }
                    val melody2 = exec { !head1; !tail2 }


                    repeat(3) { !melody1 }
                    repeat (3) { !melody2; !melody1 }

                    !head1; C*4+1 // replace({ C*4+1 }, { C*16+1 })
//                }


                // !accompaniment; higher { C*16 }

//                val head2 = def { A.dot; A/2; E+1; A; O/2; B/2; B; F+1; B }
//                val tail3 = def { higher { C.dot; C/2; G; C; O/2;  C/2; C; G; C } }
//                val tail4 = def { higher { C.dot; C/2; G; C }; tailSuffix }
//
//                val melody3 = exec { !head2; !tail1 }
//                val melody4 = exec { !head2; !tail3 }
//                val melody5 = exec { !head2; !tail4 }
//
//                !melody5; !melody3; !melody4; !melody5
//                !melody4; !melody3; !melody4; !melody5
//
//                !head2; higher {  C*16 }

                end
            }
        }
    }

    apply("src/main/resources/mdsl.mid") {
//        val e by E[5] into "e"
//        val d by D[5] into "d"
//        val c by C[5] into "c"

//        G; e; d; c; G; G; G;
//        G; e; d; c; A; A; A;
//
//        C..B
//
//        val a: Int = 455559536
//        println(a)

//        defaultNoteDuration = 4
//
////        !123956979
//        val pi = BigDecimal("-3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")
//
//        -pi
//
//
//        (5 to .3) {
//
//        }
//
//        (D to A) {
//
//        }



        //~BigInteger("1")

//        list.forEach(::println)

//        !PI
//        BigInteger("33333333333333333333333333333333333333333")
//
//        a()

    }
}


//fun test2() {
//    val mdsl = MDSL()
//    var minimsTicks = 960
////    minimsTicks = 839
//    val clock: Byte = 18
//
//    with(mdsl) {
////        timeSignature = 4 to 4
////        keySignature(F, major)
//        program = MDSL.instrument.violin
//        bpm = 120
//        keySignature(C, major)
//
////        val d by -D/2+2 into "d"
////        val ca by D into "ca"
////
////        d*4
////
//        val Am by A+B+C into "Am"
//        val Bm by A+D+C into "Bm"
//        val Cm by C+E+G into "Cm"
//
//        majorScale
//        O
//
//        majorChord
//
//        !""
//
//
//        val symbol by C triad majorChord
//
//
//
////        val j by Cm.sus2[6]*5 into "j"
////
////        j
////        val c by C into "c"
//
//
////
////        E {
////             C..B under majorScale
////        }
//
////        val c = atMainKeySignature {
////            C D E
////        }
////
////        println(c)
////
////        D {
//////            D C D
////            C {
//////                C; D; E
////                val Fd by C ninths add into "Fd"
////                Fd
////        val p by C.add9 + 1 into "p"
////        p[0] += 4
////
////
////
////
//////        println("="+p[0]+=0)
////
////        D seventh majorChord
////
////        D
//
//
//
//
//
//        (C(major) to D(minor)) {
////            Am; C; E; G; A
//
//            C..B under majorScale
//        }
//
////        C..B under minorScale
//
////        (C to D) {
////            Bm
////        }
//
//
//
//
//
////        val a1 by D seventh decreasedSeventh into "a1"
////        a1
////            }
////        }
////
////        Am and Bm
////
////        Bm; Am
//
////        println(">>>"+entrustc)
////
////        val d = Am + Bm
////////
////////        println(d.note_list)
////////
////        val ab by d into "ab"
////////
////        val cc by ab + C into "cc"
//
//
////        cc
////
////        E; D
////
////        val Cd by C+E+G into "Cd"
////
////        Cd / E
//
////        A + B + C
//
////        val Cm by C+E+G into "Cm"
////        val V by Cm into "V"
////        V
////        Cm.sus4
////        V
////
////        Am; Cm
//
//
////        Am + Bm
////        Am
//
//
//
//
//
//
//
//
//
//
////        A triad majorChord
//
////
////        Fd
//
//
////        println(A)
////
////        D relative {
////
////        }
//
//
//        list.forEach(::println)
//
////        println("========")
////        Am.note_list.forEach(::println)
////        println("========")
////        Bm.note_list.forEach(::println)
//
////        (C to D) {
////
////        }
//
//
//
//
////        A B C D D
////
////        C..E under diatonic
////        D[4]
////        !D + 1
////        (C to A) {
//           // G; E*2+1; E+1; G; D*2+1; D+1; G; C*2+1; C*2+1; C*2+1; D*2+1
//
////        }
//    }
//
//    val midi = MidiFile()
//    midi.append {
//        track {
//            meta(MetaEventType.META_TEMPO, args = bpm(mdsl.bpm))
//
//            mdsl.keySignature?.let {
//                meta(MetaEventType.META_KEY_SIGNATURE, (abs(it.first.semitone) or 0x80).toByte(), it.second.toByte())
//            }
//
//            mdsl.timeSignature?.let {
//                meta(MetaEventType.META_TIME_SIGNATURE, it.first.toByte(), log2(it.second.toDouble()).toInt().toByte(), clock, 8)
//            }
//
//            meta(MetaEventType.META_END_OF_TRACK)
//        }
//
//        track {
//            messaged(EventType.program_change, mdsl.program.id.toByte())
//
//            for (i in mdsl.list) {
//                val note = i.code
//                messageno(note, 0, i.velocity)
//                messagenf(note, (minimsTicks * 2 * i.duration).toInt(), i.velocity)
//            }
//
//            meta(MetaEventType.META_END_OF_TRACK)
//        }
//    }
//
//    midi.save("src/main/resources/mdsl.mid")
//}
//
//fun a() {
//
//    with(MDSL()) {
//
//        keySignature(D, major)
//
//        (+D)(major) {
//            C; D; E
//        }
//
//        (F)(major) {
//            C; D; E
//        }
//
//
//
////        (C..G) step 1// major
////        (C to D) {
////            C D E F G A B
////        }
//
//
////        var n = arrayOf(2,2,1,2,2,2,1)
////        var c = 0
////        for (i in 0..32) {
////            var p = 0
////            p+=n[c % n.size]
////            println("$p, ${n[c % n.size]}")
////            c++
////        }
//
////        var from = 24
////        var to = 35
////        var i = from
////        var loopc = 0
////
////        while (i <= to) {
////            println(i)
////            i += n[loopc%n.size]
////            loopc++
////        }
//        list.forEach {
//            println(it)
//        }
//    }
//}