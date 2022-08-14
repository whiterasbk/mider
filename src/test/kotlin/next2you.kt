import whiter.music.mider.dsl.apply
import whiter.music.mider.dsl.play

fun main(args: Array<String>) {
    apply("src/test/resources/next to you.xml") {
        pitch = 5
        bpm = 120
        duration = 1.0/8
        keySignature = "Eminor"
        "Eminor" {


            val a0 = def { G; E; G }
            val a1 = def { A*2.dot; G/2; F/2 }
            val a2 = def { G*2.dot; A/2; G/2 }
            val a3 = def { F; E; D; F }
            val a4 = def { F*2.dot; A/2; G/2 }

            val p1 = def { +a0; B; +a1 }

            repeat {
                +p1
                +a2
                if (repeatCount == 1)
                    +a3
                else {
                    A; C+1; B; F
                }
            }

            val p2 = exec { +a0; D+1; +a1 }
            val p3 = exec { +a4; D*2+1; B; A }
            val p4 = exec { G; B-1; E; B; +a1 }
            val p5 = exec { +a4; F; C+1; B; F }

            +p1
            +a2; +a3
            +p1
            +p5
            +p2
            +p3
            +p4
            +p5

            val a5 = def { E; C+1 }
            '2' {
                3*G; F
                2*E; G; F/2; G/2

                2*A; B; C+1
                2*B; A; val a6 = exec { A/2; G/2 }

                3*G; A
                +a5; B; +a6
                B; A; D+1; C/2+1; B/2
            }
            B*2; C+1; repeat { B; A }; G
            E*2; +a5; B*2; G; A
            val A2d = exec { A*2.dot }; F; val a7 = exec { G; A; B }; C+1
            val p6 = exec { B*2.dot; G; F*2; G; A }
            +A2d; 2*A; G; F; G
            E*2.dot; +a5; B; A; G
            +A2d; 2*F; +a7
            +p6

            '2' {
                B; A; C+1; B
            }

        }
    }
}