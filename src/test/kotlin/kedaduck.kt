import whiter.music.mider.dsl.apply
import whiter.music.mider.dsl.play

fun main(args: Array<String>) {
    play {
        (-E) {


            // ">g;-Emaj>gaC+C-a-C-D+DEG+G-E-G-E+DEC+C-a-C-D+DCagaC+C-a-C-D+DEG+G-E-G-E+DEC+C-a-C-D+DCagaC+C-a-C-D+DEG+G-E-G-E+DEC+C-a-C-D+DCa"
        }



        useMode("-E") {
            repeat(3) {
                G-1; A-1; C*2; '8' { C; A-1; C }; D*2
                D; E; G*2; '8' { G; E; G}; E*2
                D; E; C*2; '8' { C; A-1; C }; D*2
                D; C; A-1
            }
        }
    }
}