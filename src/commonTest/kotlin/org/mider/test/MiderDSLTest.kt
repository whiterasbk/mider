package org.mider.test

import org.mider.cast
import org.mider.descr.*
import org.mider.dsl.MiderDSL
import kotlin.test.*

class MiderDSLTest : RunningTestInMusicScore() {
    @Test
    fun testBasic() {
        val dsl = dsl {
            A/2; C*2; C%67; B+1; C-1; D[3,.5]; E.dot; +F; -G
            val p by D into "p"
            p
            G["ai2"]
            !C
        }

        assertEquals(listOf(
            generate("A", duration/2),
            generate("C", duration*2),
            generate("C", velocity = 67),
            generate("B5"),
            generate("C3"),
            generate("D3", duration*2),
            generate("E", duration*1.5),
            generate("#F"),
            generate("#F"),
            generate("D"),
            "[67=G4|0.25|100<lyric: ai2>]",
            generate("C")
        ).jts(), dsl.jts())

        assertTrue(dsl[-2].cast<Note>().attach?.lyric == "ai2")
        assertTrue(dsl.last().cast<Note>().isNature)
    }

    @Test
    fun testChord() {
        val dsl = dsl {
            C+E+G; A; A+D +1; A+E+F - F
            (C+D)*2; (C+D)/2
            (C+E+G)/E
            val a by C+E+G into "a"
            a

            a.sus
            a.sus2

            a - 1
            a*2
            a.ascending
            a.downward
        }

        assertEquals(listOf(
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
            ).jts(" "),
            generate("A"),
            "Chord: " + listOf(
                generate("A5"),
                generate("D5")
            ).jts(" "),
            "Chord: " + listOf(
                generate("A"),
                generate("E")
            ).jts(" "),
            "Chord: " + listOf(
                generate("C", duration*2),
                generate("D")
            ).jts(" "),
            "Chord: " + listOf(
                generate("C", duration/2),
                generate("D")
            ).jts(" "),
            "Chord: " + listOf(
                generate("C5"),
                generate("E"),
                generate("G"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("F"),
                generate("G"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("D"),
                generate("G"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C3"),
                generate("E3"),
                generate("G3"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C", duration * 2),
                generate("E"),
                generate("G"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
            ).jts(" ")
        ).jts(), dsl.jts())

        assertEquals(ArpeggioType.Ascending, dsl.container.mainList[dsl.container.mainList.lastIndex - 1].cast<Chord>().arpeggio)
        assertEquals(ArpeggioType.Downward, dsl.container.mainList.last().cast<Chord>().arpeggio)
    }

    @Test
    fun testGenerateChord() {
        val dsl = dsl {
            A triad majorChord
            C seventh majorChord
            C add9 majorChord
            C ninths majorChord
            C ninths minorChord
        }

        assertEquals(listOf(
            "Chord: " + listOf(
                generate("A"),
                generate("#C5"),
                generate("E5")
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
                generate("B")
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
                generate("D5")
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("E"),
                generate("G"),
                generate("B"),
                generate("D5"),
            ).jts(" "),
            "Chord: " + listOf(
                generate("C"),
                generate("#D"),
                generate("G"),
                generate("#A"),
                generate("D5")
            ).jts(" ")
        ).jts(), dsl.jts())
    }

    @Test
    fun testScale() {
        val dsl = dsl {
            C..B
            C..B under majorScale
            C..F step 2
        }

        assertEquals(listOf(
            generate("C"),
            generate("#C"),
            generate("D"),
            generate("#D"),
            generate("E"),
            generate("F"),
            generate("#F"),
            generate("G"),
            generate("#G"),
            generate("A"),
            generate("#A"),
            generate("B"),

            generate("C"),
            generate("D"),
            generate("E"),
            generate("F"),
            generate("G"),
            generate("A"),
            generate("B"),

            generate("C"),
            generate("D"),
            generate("E"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testGlissando() {
        val dsl = dsl {
            C gliss E gliss D[5]
            (C gliss G).wave
        }

        assertEquals(listOf(
            "Glissando: " + listOf(
                generate("C"),
                generate("E"),
                generate("D5"),
            ).jts(" "),
            "Glissando: " + listOf(
                generate("C"),
                generate("G")
            ).jts(" "),
        ).jts(), dsl.jts())

        assertTrue(dsl.container.mainList.last().cast<Glissando>().isWave)
    }

    @Test
    fun testAppoggiatura() {
        val dsl = dsl {
            C appoggiatura E+1
            (D appoggiatura A).back
        }

        assertEquals(listOf(
            "Appoggiatura: " + listOf(
                generate("C"),
                generate("E5")
            ).jts(" "),
            "Appoggiatura: " + listOf(
                generate("D"),
                generate("A")
            ).jts(" "),
        ).jts(), dsl.jts())

        assertTrue(!dsl.container.mainList.last().cast<Appoggiatura>().isFront)
    }

    @Test
    fun testRest() {
        val dsl = dsl {
            O; O/2; O*2
            O + A
        }

        assertEquals(listOf(
            "[Rest|$duration]",
            "[Rest|${duration/2}]",
            "[Rest|${duration*2}]",
            "[Rest|$duration]",
            generate("A")
        ).jts(), dsl.jts())
    }

    @Test
    fun testInserted() {
        var list: MutableList<InMusicScore>? = null
        var list2: MutableList<InMusicScore>? = null
        val dsl = dsl {
            A
            list = inserted {
                B
                list2 = inserted {
                    C
                }
            }
            D
        }

        assertEquals(listOf(
            generate("A"),
            generate("D")
        ).jts(), dsl.jts())

        assertEquals(listOf(
            generate("B"),
        ).jts(), list?.jts())

        assertEquals(listOf(
            generate("C"),
        ).jts(), list2?.jts())
    }

    @Test
    fun testScope() {
        val dsl = dsl {
            C
            scope {
                duration = .5
                pitch = 5
                velocity = 69
                D
            }
            E
        }

        assertEquals(listOf(
            generate("C"),
            generate("D5", .5, velocity = 69),
            generate("E"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testVelocity() {
        val dsl = dsl {
            C
            velocity(55) {
                D
            }
            E
        }

        assertEquals(listOf(
            generate("C"),
            generate("D", velocity = 55),
            generate("E"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testPitch() {
        val dsl = dsl {
            C
            6 {
                D
            }
            E
        }

        assertEquals(listOf(
            generate("C"),
            generate("D6"),
            generate("E"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testCharDuration() {
        val dsl = dsl {
            C
            '2' {
                D
            }
            E
        }

        assertEquals(listOf(
            generate("C"),
            generate("D", duration*2),
            generate("E"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testIntXIM() {
        val dsl = dsl {
            3 * (A+1)
        }

        assertEquals(listOf(
            generate("A5"),
            generate("A5"),
            generate("A5")
        ).jts(), dsl.jts())
    }

    @Test
    fun testUseMode() {
        val dsl = dsl {
            "Cmin" {
                C..B under majorScale
            }
            "Fmin" {
                C..B under majorScale
                !A
            }
            "Amin" {
                C..B under majorScale
            }
            "B" {
                C..B under majorScale
            }
            "Emajor" {
                C..B under majorScale
                !D
            }
        }

        assertEquals(listOf(
            generate("C"),
            generate("D"),
            generate("#D"),
            generate("F"),
            generate("G"),
            generate("#G"),
            generate("#A"),

            generate("C"),
            generate("#C"),
            generate("#D"),
            generate("F"),
            generate("G"),
            generate("#G"),
            generate("#A"),
            generate("A"),

            generate("C"),
            generate("D"),
            generate("E"),
            generate("F"),
            generate("G"),
            generate("A"),
            generate("B"),

            generate("B"),
            generate("#C5"),
            generate("#D5"),
            generate("E5"),
            generate("#F5"),
            generate("#G5"),
            generate("#A5"),

            generate("E"),
            generate("#F"),
            generate("#G"),
            generate("A"),
            generate("B"),
            generate("#C5"),
            generate("#D5"),
            generate("D"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testRepeat() {
        val dsl = dsl {
            repeat {
                when(repeatCount) {
                    1 -> A
                    2 -> B[5]
                }

                repeat {
                    when(repeatCount) {
                        1 -> C
                        2 -> D
                    }
                }
            }
        }

        assertEquals(listOf(
            generate("A"),
            generate("C"),
            generate("D"),
            generate("B5"),
            generate("C"),
            generate("D"),
        ).jts(), dsl.jts())
    }

    @Test
    fun testChordInterval() {
        val dsl = dsl {
            "Cminor" { A; B; C }
            withInterval(F[5] - A) {
                A*2; G; A; B*2
            }
        }

        assertEquals(listOf(
            generate("#G"),
            generate("#A"),
            generate("C"),
            "Chord: " + listOf(generate("A", duration*2), generate("F5",duration*2)).jts(" "),
            "Chord: " + listOf(generate("G"), generate("#D5")).jts(" "),
            "Chord: " + listOf(generate("A"), generate("F5")).jts(" "),
            "Chord: " + listOf(generate("B", duration*2), generate("G5",duration*2)).jts(" "),
        ).jts(), dsl.jts())
    }

    @Test
    fun testDefAndExec() {
        val dsl = dsl {
            val p = def { C }
            val l = exec { B }
            +l; +p
        }

        assertEquals(listOf(
            generate("B"),
            generate("B"),
            generate("C")
        ).jts(), dsl.jts())
    }

    @Test
    fun testTracks() {
        val dsl = dsl {
            track {
                A
            }
        }

        assertEquals(listOf(
            generate("A")
        ).jts(), dsl.otherTracks.last().jts())
    }

    fun dsl(block : MiderDSL.() -> Unit): MiderDSL {
        val ret = MiderDSL()
        ret.block()
        return ret
    }
}
