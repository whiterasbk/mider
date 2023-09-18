
package org.mider.test

import org.junit.jupiter.api.Test
import org.mider.MidiInstrument
import org.mider.dsl.MiderDSL
import org.mider.dsl.play

//fun main(args: Array<String>) {
//    val used = mutableMapOf<Int, String?>(
//        1 to "null",
//        2 to "null",
//        3 to "null",
//        4 to "null",
//        5 to "null",
//        6 to "null",
//        8 to "null",
//        9 to "null",
//        11 to "null",
//        12 to "null",
//        13 to "null",
//        14 to "null",
//        15 to "null"
//    )
//
//    used.filter { it.value == null }.keys.first().println()
//}


class TestRun {
    @Test
    fun `place holder`() {

    }
}

fun main() {
    play {
        musicbox()
    }
//    play {
//        // 碎月()
//
//
//
//
//
//
//
//        // +"多 啦低 升啦[n b _]"
//        // B; C; A["1 2"]
//        // container.mainList.forEach(::println)
////        碎月()
//    }








//    val pc = produceCore(">g;Bmin;sing>c[两]d[只]e[老]c[虎]")
//    val dsl2MusicXml = Dsl2MusicXml(pc.miderDSL)
//    println(dsl2MusicXml.toString().formatXml())
//
////    val j = produceCore(">g;sing:zh:f1;/2>abbbbbbbb%127")
////    println(j.singSong)
//    playDslInstance(miderDSL = pc.miderDSL)
//    playDslInstance(miderDSL = pc.miderDSL)

//    val k = produceCore("""
//            >240b;Bmin>faaabDba | b-D-~~Db-a-a++ | F~~~ GFED | E-~~~EE-F-E++ | FFF-F F-GFED | bDD b-a- a++ | FFF-F F-GFED | a-~~~EC D++-+
//        """.trimIndent())
//
//    k.miderDSL.container.mainList.forEach(::println)
//    println(k.miderDSL.container.mainList.size)
//    fromDslInstance(k.miderDSL).save("src/test/resources/4wrong.mid")
//    playDslInstance(miderDSL = k.miderDSL)
//    playDslInstance(miderDSL = k.miderDSL)

//    val result = produceCore(""">120b>
//
//    """.trimMargin())
//    result.logs.forEach{ println(it) }
//    playDslInstancev2(miderDSL = result.miderDSL)

//
//    val dsLv2 = MiderDSL()
//
//    with(dsLv2) {
//
//        bpm = 240
//        keySignature = "Bmin"
//
////        """
////            1[两]2[只]3[老]1[虎]
////            1[两]2[只]3[老]1[虎]
////            3[跑]4[得]5[快]+
////            3[跑]4[得]5[快]+
////
////            5[一]-6[只]-5[没]-4[有]-3[心]1[巴]
////            5[一]-6[只]-5[没]-4[有]-3[上]1[巴]
////
////            1[真]5[奇]↓1[怪]+
////            1[真]5[奇]↓1[怪]+
////
////        """
//        "Bmin" {
//            +"""
//                faaabDba | b-D-~~Db-a-a++ | F~~~ GFED | E-~~~EE-F-E++ |
//                 FFF-F F-GFED | bDD b-a- a++ | FFF-F F-GFED | a-~~~EC D++-+
//            """.trimIndent()
//        }
//
//
//    }
//
//    val mx = Dsl2MusicXml(dsLv2)
//
//    dsLv2.container.mainList.forEach(::println)
//
//    File("src/test/resources/3format.xml").writeText(mx.toString().formatXml())
//
//    playDslInstance(miderDSL = dsLv2)


    play {

//        program = MidiInstrument.musicbox

//        bpm = 80
//        O*2
//        A;B;C
//        (C gliss B+2 gliss B+4).hasBlack
//
//        bpm = 300
//
//        A;B;C




//
//        C;G
//        (C appoggiatura G).back
//        D


//        C;D;E;F;G;A;B
////        (C + E + G)
//        C..B under majorScale
//        (C + E + G).ascending
//        (C + E + G).downward
//
//        C;E;G



//        program = MidiInstrument.musicbox
//
//        C..B under majorScale
//
//
//        track {
//
//            program = MidiInstrument.oboe
//
//            C..B under majorScale; A
//        }







//        O * 4
//        C+E+G
//
//        bpm = 500
//
//        for (i in 1..1000000) {
//            C..B under majorScale
//        }

//        {
//            E;E;E;E
//            ((E + F + B)).arpeggio = ArpeggioType.Ascending
//            C;C;C
//
//            D;D;D;D
//        }
//        C; D
//        A;B;A
//
//        C;E;G;A

//        D; B; C; A; D
//        bpm = 90
//        duration = 1.0/8
//        "Cminor" {
//                // 使用 C 小调
//                repeat {
//                    G; B
//                    5 { C; E; F * 2; E; F; G * 2; E; C }
//                    B; G
//                    5 { E; F; C * 2; C; E; F * 2; E; F; G * 2; B; C + 1 }
//
//                    instrument(MidiInstrument.oboe) {
//                        6 { E; D; C / 4; D / 4; C / 2; B - 1; C * 2 }
//                    }



//
//                    5 {
//                        B; G; F*2; G; E; F*2; E; F; G.dot; C/2; E/2; F/2; E; C*2; C; B-1
//                        C.dot; B/4-1; C/4; E; F; G; F; B*2-1; C*4; G-1; B-1; C; E
//                    }
//
//                    val P0 = exec { withInterval(F[5]-A) { A * 2; G; A; B * 2 } }
//
//                    5 { E; C; B-1; G-1; E; F; C*2; C; E }; +P0
//                    6 { B-1; C; E; D; C/4; D/4; C/2; B-1; C*2 }
//                    5 {
//                        C+B; B-1+G; A*2-1+F; B-1+G; A-1+E; B*2-1+F; G-1+E; A-1+F
//                        C.dot+G; C/2; E/2; F/2; E; C*2; C+E; B-1+F; C.dot+E; B/4-1; C/4
//                        E; F; G; F; G*2+B; C*4+1+E
//                    }
//                    G; B
//                    5 {
//                        C; F; B; C+1; G; F; G*2+B; E; F; D; E; D/4; E/4; D/2; B-1; F*2-1+C
//                    }
//                    G; B
//                    5 {
//                        C; E; A-1+F; G-1+E; A.dot-1+F; E/2; F/2; G/2; E; D.dot+F; if (repeatCount == 1) B/2-1 else C/2 + E; D+F; B; E*2+G; G; B
//                        C*2+1+A; B; C+1; G*2.dot; E; F; G; F/4; G/4; F/2; E; C*2
//                        C; B-1; C.dot; B/4-1; C/4; E; F; G; F; B*2
//                        if (repeatCount == 1) { C*4+1+E+G } else { C*16+1+E+G }
//                    }
//                }
//            }
//
//        track {
//            program = MidiInstrument.whistle
//            "Cmin" {
//                lower {
//                    G; B
//                    higher { C; E; F * 2; E; F; G * 2; E; C }
//                    B; G
//                    higher { E; F; C * 2; C; E; F * 2; E; F; G * 2; B; C + 1 }
//                }
//            }
//        }


//
//        track {
//            program = MidiInstrument.piano
//            "Cminor" {
//
////            O; 11*O; A; 24*O; B; A;B
//
//                // 使用 C 小调
//                repeat {
//                    G; B
//                    5 { C; E; F*2; E; F; G*2; E; C }
//                    B; G
//                    5 { E; F; C*2; C; E; F*2; E; F; G*2; B; C+1 }
//                    6 { E; D; C/4; D/4; C/2; B-1; C*2 }
//                    5 {
//                        B; G; F*2; G; E; F*2; E; F; G.dot; C/2; E/2; F/2; E; C*2; C; B-1
//                        C.dot; B/4-1; C/4; E; F; G; F; B*2-1; C*4; G-1; B-1; C; E
//                    }
//
//                    val P0 = exec { withInterval(F[5]-A) { A * 2; G; A; B * 2 } }
//
//                    5 { E; C; B-1; G-1; E; F; C*2; C; E }; +P0
//                    6 { B-1; C; E; D; C/4; D/4; C/2; B-1; C*2 }
//                    5 {
//                        C+B; B-1+G; A*2-1+F; B-1+G; A-1+E; B*2-1+F; G-1+E; A-1+F
//                        C.dot+G; C/2; E/2; F/2; E; C*2; C+E; B-1+F; C.dot+E; B/4-1; C/4
//                        E; F; G; F; G*2+B; C*4+1+E
//                    }
//                    G; B
//                    5 {
//                        C; F; B; C+1; G; F; G*2+B; E; F; D; E; D/4; E/4; D/2; B-1; F*2-1+C
//                    }
//                    G; B
//                    5 {
//                        C; E; A-1+F; G-1+E; A.dot-1+F; E/2; F/2; G/2; E; D.dot+F; if (repeatCount == 1) B/2-1 else C/2 + E; D+F; B; E*2+G; G; B
//                        C*2+1+A; B; C+1; G*2.dot; E; F; G; F/4; G/4; F/2; E; C*2
//                        C; B-1; C.dot; B/4-1; C/4; E; F; G; F; B*2
//                        if (repeatCount == 1) { C*4+1+E+G } else { C*16+1+E+G }
//                    }
//                }
//            }
//        }
    }
}

fun MiderDSL.musicbox() {
    "Cminor" {
        bpm *= 2

        instrument(MidiInstrument.musicbox) {
            repeat {
                G; B
                5 { C; E; F * 2; E; F; G * 2; E; C }
                B; G
                5 { E; F; C * 2; C; E; F * 2; E; F; G * 2; B; C + 1 }

//                instrument(MidiInstrument.oboe) {
                    6 { E; D; C / 4; D / 4; C / 2; B - 1; C * 2 }
//                }

                5 {
                    B; G; F * 2; G; E; F * 2; E; F; G.dot; C / 2; E / 2; F / 2; E; C * 2; C; B - 1
                    C.dot; B / 4 - 1; C / 4; E; F; G; F; B * 2 - 1; C * 4; G - 1; B - 1; C; E
                }

                val P0 = exec { withInterval(F[5] - A) { A * 2; G; A; B * 2 } }

                5 { E; C; B - 1; G - 1; E; F; C * 2; C; E }; +P0
                6 { B - 1; C; E; D; C / 4; D / 4; C / 2; B - 1; C * 2 }
                5 {
                    C + B; B - 1 + G; A * 2 - 1 + F; B - 1 + G; A - 1 + E; B * 2 - 1 + F; G - 1 + E; A - 1 + F
                    C.dot + G; C / 2; E / 2; F / 2; E; C * 2; C + E; B - 1 + F; C.dot + E; B / 4 - 1; C / 4
                    E; F; G; F; G * 2 + B; C * 4 + 1 + E
                }
                G; B
                5 {
                    C; F; B; C + 1; G; F; G * 2 + B; E; F; D; E; D / 4; E / 4; D / 2; B - 1; F * 2 - 1 + C
                }
                G; B
                5 {
                    C; E; A - 1 + F; G - 1 + E; A.dot - 1 + F; E / 2; F / 2; G / 2; E; D.dot + F; if (repeatCount == 1) B / 2 - 1 else C / 2 + E; D + F; B; E * 2 + G; G; B
                    C * 2 + 1 + A; B; C + 1; G * 2.dot; E; F; G; F / 4; G / 4; F / 2; E; C * 2
                    C; B - 1; C.dot; B / 4 - 1; C / 4; E; F; G; F; B * 2
                    if (repeatCount == 1) {
                        C * 4 + 1 + E + G
                    } else {
                        C * 16 + 1 + E + G
                    }
                }
            }
        }
    }
}

fun MiderDSL.碎月() {
    "Cminor" {

        bpm *= 2

        // 使用 C 小调
        repeat {
            G; B
            5 { C; E; F * 2; E; F; G * 2; E; C }
            B; G
            5 { E; F; C * 2; C; E; F * 2; E; F; G * 2; B; C + 1 }

            instrument(MidiInstrument.oboe) {
                6 { E; D; C / 4; D / 4; C / 2; B - 1; C * 2 }
            }

            5 {
                B; G; F * 2; G; E; F * 2; E; F; G.dot; C / 2; E / 2; F / 2; E; C * 2; C; B - 1
                C.dot; B / 4 - 1; C / 4; E; F; G; F; B * 2 - 1; C * 4; G - 1; B - 1; C; E
            }

            val P0 = exec { withInterval(F[5] - A) { A * 2; G; A; B * 2 } }

            5 { E; C; B - 1; G - 1; E; F; C * 2; C; E }; +P0
            6 { B - 1; C; E; D; C / 4; D / 4; C / 2; B - 1; C * 2 }
            5 {
                C + B; B - 1 + G; A * 2 - 1 + F; B - 1 + G; A - 1 + E; B * 2 - 1 + F; G - 1 + E; A - 1 + F
                C.dot + G; C / 2; E / 2; F / 2; E; C * 2; C + E; B - 1 + F; C.dot + E; B / 4 - 1; C / 4
                E; F; G; F; G * 2 + B; C * 4 + 1 + E
            }
            G; B
            5 {
                C; F; B; C + 1; G; F; G * 2 + B; E; F; D; E; D / 4; E / 4; D / 2; B - 1; F * 2 - 1 + C
            }
            G; B
            5 {
                C; E; A - 1 + F; G - 1 + E; A.dot - 1 + F; E / 2; F / 2; G / 2; E; D.dot + F; if (repeatCount == 1) B / 2 - 1 else C / 2 + E; D + F; B; E * 2 + G; G; B
                C * 2 + 1 + A; B; C + 1; G * 2.dot; E; F; G; F / 4; G / 4; F / 2; E; C * 2
                C; B - 1; C.dot; B / 4 - 1; C / 4; E; F; G; F; B * 2
                if (repeatCount == 1) {
                    C * 4 + 1 + E + G
                } else {
                    C * 16 + 1 + E + G
                }
            }
        }
    }
}