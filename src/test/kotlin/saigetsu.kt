import whiter.music.mider.dsl.apply

fun main(args: Array<String>) {
    apply("src/test/resources/saigetsu.mid") {
        bpm = 90

        C(minor) { // 使用 C 小调
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
            }
        }
    }
}