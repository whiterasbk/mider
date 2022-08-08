
import whiter.music.mider.MidiInstrument
import whiter.music.mider.descr.ArpeggioType
import whiter.music.mider.dsl.DispatcherControlled
import whiter.music.mider.ppply

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

fun main(args: Array<String>) {
    ppply("src/test/resources/1saigetsu.mid") {

//        program = MidiInstrument.musicbox

        O*2
        A;B;C
        (C gliss B+2 gliss B+4).hasBlack



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