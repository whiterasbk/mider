import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import whiter.music.mider.*
import whiter.music.mider.code.toInMusicScoreList
import whiter.music.mider.descr.*
import whiter.music.mider.dsl.InMusicScoreContainer
import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.noteNameFromCode
import whiter.music.mider.xml.*
import java.io.File

class TestsXml {
    @Test
    @DisplayName("testNodeToString")
    fun testNodeToString() {

        val tagName = "tag"

        val t1 = Node(tagName)
        assertEquals("<$tagName></$tagName>", t1.toString())

        val t2 = Node(tagName, mutableMapOf("attr" to "1"), "content")
        assertEquals("<$tagName attr=\"1\">content</$tagName>", t2.toString())

        val t3 = Node(tagName, mutableMapOf("attr" to "1"), t1)
        assertEquals("<$tagName attr=\"1\"><$tagName></$tagName></$tagName>", t3.toString())

        val t4 = Node(tagName, mutableMapOf("attr" to "1"), t1, t1)
        assertEquals("<$tagName attr=\"1\"><$tagName></$tagName><$tagName></$tagName></$tagName>", t4.toString())

        val t5 = Node(tagName, t1, t1)
        assertEquals("<$tagName><$tagName></$tagName><$tagName></$tagName></$tagName>", t5.toString())

        val t6 = Node(tagName, "attr" to "1")
        assertEquals("<$tagName attr=\"1\"></$tagName>", t6.toString())

        val t7 = Node(tagName, "content")
        assertEquals("<$tagName>content</$tagName>", t7.toString())

        val t8 = Node(tagName, 1)
        assertEquals("<$tagName>1</$tagName>", t8.toString())

        val t9 = Node(tagName, true)
        assertEquals("<$tagName>true</$tagName>", t9.toString())
    }

    @Test
    @DisplayName("testAttributes")
    fun testAttributes() {
        val attr = AttributesElement()

        attr.addDivisions()
        attr.addKeySignature()
        attr.addTimeSignature()

        val expected = """
              <attributes>
                <divisions>1</divisions>
              <key>
                <fifths>0</fifths>
                <mode>major</mode>
              </key>
              <time>
                <beats>4</beats>
                <beat-type>4</beat-type>
                </time>
              </attributes> 
            """.trimLines()

        assertEquals(expected.formatXml(), attr.toString().formatXml())
    }

    @Test
    @DisplayName("testNotes")
    fun testNotes() {

        val expected = """
        <note>
            <pitch>
                <step>E</step>
                <octave>5</octave>
                <alter>-1</alter>
            </pitch>
            <duration>24</duration>
            <tie type="start"></tie>
            <type>whole</type>
            <lyric>
                <syllabic>end</syllabic>
                <text>meil</text>
                <extend></extend>
            </lyric>
        </note>
        """.trimLines()

        val note = NoteElement("E", 5, 24, -1)
        note.addTie("start")
        note.addType()
        note.addLyric("meil")

        assertEquals(expected.formatXml(), note.toString().formatXml())
    }

    @Test
    @DisplayName("ScorePartWiseElement")
    fun testScorePartWiseElement() {
        val musicXml = MusicXml(false).part {
            measure {
                attr(AttributesElement().addDefault())
                note(NoteElement("C", 4, 4).addType())
            }
        }

        val expected = """
            <score-partwise version="3.0">
                <part-list>
                    <score-part  id="P1">
                        <part-name>Music</part-name>
                    </score-part>
                </part-list>
                <part  id="P1">
                    <measure number="1">
                        <attributes>
                            <divisions>1</divisions>
                            <key>
                                <fifths>0</fifths>
                                <mode>major</mode>
                            </key>
                            <time>
                                <beats>4</beats>
                                <beat-type>4</beat-type>
                            </time>
                            <clef>
                                <sign>G</sign>
                                <line>2</line>
                            </clef>
                        </attributes>
                        <note>
                            <pitch>
                                <step>C</step>
                                <octave>4</octave>
                            </pitch>
                            <duration>4</duration>
                            <type>whole</type>
                        </note>
                    </measure>
                </part>
            </score-partwise>        
        """.trimLines()

        assertEquals(expected.formatXml(), musicXml.toString().formatXml())
    }

    @Test
    @DisplayName("ScorePartWiseElement")
    fun testOutputFile() {
        val path = "build/out.xml"
        MusicXml().part {
            measure {
                attr(AttributesElement().addDefault())
                note(NoteElement("C", 4, 4).addType().addLyric("nih"))
                note(NoteElement("D", 4, 4).addType().addLyric("nih"))
                note(NoteElement("F", 4, 4).addType().addLyric("nih"))
                note(NoteElement("G", 4, 4).addType().addLyric("nih"))
                note(NoteElement("A", 4, 4).addType().addLyric("nih"))
                note(NoteElement("B", 4, 4).addType().addLyric("nih"))
            }
        }.save(path)

        assert(File(path).exists())
    }
}

class TestMiderCodeParser : ABTestInMusicScore() {

    @Test
    @DisplayName("testNormal")
    fun testNormal() {
        val list = toInMusicScoreList("cdefgabCDEFGAB", useMacro = false)
        val expected = listOf(
            generate("C"),
            generate("D"),
            generate("E"),
            generate("F"),
            generate("G"),
            generate("A"),
            generate("B"),
            generate("C5"),
            generate("D5"),
            generate("E5"),
            generate("F5"),
            generate("G5"),
            generate("A5"),
            generate("B5")
        ).joinToString("\n")

        assertEquals(expected, list.joinToString("\n"))

        val numList = toInMusicScoreList("1234567 1i2i3i4i5i6↑7↑", isStave = false, useMacro = false)
        assertEquals(list, numList)
    }

    @Test
    @DisplayName("testDuration")
    fun testDuration() {
        val list = toInMusicScoreList("c+d-e. c/3", useMacro = false)

        assertEquals(listOf(
            generate("C", duration * 2),
            generate("D", duration / 2),
            generate("E", duration * 1.5),
            generate("C", duration / 3)
        ).joinToString("\n"), list.joinToString("\n"))

        assertEquals(3.0, list[3].duration.denominator)

        val numList = toInMusicScoreList("1+2-3. 1/3", useMacro = false, isStave = false)
        assertEquals(list, numList)
    }

    @Test
    @DisplayName("testFlatAndSharp")
    fun testFlatAndSharp() {
        val list = toInMusicScoreList("#c\$d♭e♮fga\"b'", useMacro = false)
        val expected = listOf(
            "[61=#C4|$duration|$velocity]",
            "[61=#C4|$duration|$velocity]",
            "[63=#D4|$duration|$velocity]",
            "[65=F4|$duration|$velocity]",
            "[67=G4|$duration|$velocity]",
            "[70=#A4|$duration|$velocity]",
            "[70=#A4|$duration|$velocity]"
        ).joinToString("\n")

        assertEquals(expected, list.joinToString("\n"))
        assert(list[3].cast<Note>().isNature)

        val numList = toInMusicScoreList("#1$2b3&45♯67'", isStave = false, useMacro = false)

        assertEquals(list, numList)
    }

    @Test
    @DisplayName("testAppoggiatura")
    fun testAppoggiatura() {
        val list = toInMusicScoreList("a;d++ a++;d a%50;d%20 a;dt++", useMacro = false)

        val group1 = listOf(
            "[69=A4|${duration}|$velocity]",
            "[62=D4|${duration*4}|$velocity]"
        )

        val group2 = listOf(
            "[69=A4|${duration*4}|$velocity]",
            "[62=D4|${duration}|$velocity]"
        )

        val group3 = listOf(
            "[69=A4|${duration}|${velocity / 2}]",
            "[62=D4|${duration}|${velocity / 5}]"
        )

        assertEquals(listOf(
            "Appoggiatura: ${group1.joinToString(" ")}",
            "Appoggiatura: ${group2.joinToString(" ")}",
            "Appoggiatura: ${group3.joinToString(" ")}",
            "Appoggiatura: ${group1.joinToString(" ")}",
        ).joinToString("\n"), list.joinToString("\n"))

        assert(list[0].cast<Appoggiatura>().isFront)
        assert(list[1].cast<Appoggiatura>().isFront)
        assert(list[2].cast<Appoggiatura>().isFront)
        assert(!list[3].cast<Appoggiatura>().isFront)

        val numList = toInMusicScoreList("6;2++ 6++;2 6%50;2%20 6;2t++", useMacro = false, isStave = false)
        assertEquals(list, numList)
    }

    @Test
    @DisplayName("testChord")
    fun testChord() {
        val list = toInMusicScoreList(
            "a++:d:c" +
                "a:d--:c" +
                "a:d:c++" +
                "a%50:d%20:c%20 " +
                "a:d\":c' " +
                "a:m:m " +
                "a:d↑:c↑ " +
                "a:d:c↟" +
                "a:d:c↡",
            useMacro = false)

        val group0 = listOf(
            "[69=A4|$duration|$velocity]",
            "[62=D4|$duration|$velocity]",
            "[60=C4|$duration|$velocity]"
        )

        val group1 = listOf(
            "[69=A4|${duration*4}|$velocity]",
            "[62=D4|${duration}|$velocity]",
            "[60=C4|${duration}|$velocity]"
        )

        val group2 = listOf(
            "[69=A4|${duration}|$velocity]",
            "[62=D4|${duration/4}|$velocity]",
            "[60=C4|${duration}|$velocity]"
        )

        val group3 = listOf(
            "[69=A4|${duration}|$velocity]",
            "[62=D4|${duration}|$velocity]",
            "[60=C4|${duration*4}|$velocity]"
        )

        val group4 = listOf(
            "[69=A4|$duration|${velocity / 2}]",
            "[62=D4|$duration|${velocity / 5}]",
            "[60=C4|$duration|${velocity / 5}]"
        )

        val group5 = listOf(
            "[69=A4|$duration|$velocity]",
            "[63=#D4|$duration|$velocity]",
            "[59=B3|$duration|$velocity]"
        )

        val group6 = listOf(
            "[69=A4|$duration|$velocity]",
            "[72=C5|$duration|$velocity]",
            "[76=E5|$duration|$velocity]"
        )

        val group7 = listOf(
            "[69=A4|$duration|$velocity]",
            "[74=D5|$duration|$velocity]",
            "[72=C5|$duration|$velocity]"
        )

        assertEquals(listOf(
            "Chord: ${group1.joinToString(" ")}",
            "Chord: ${group2.joinToString(" ")}",
            "Chord: ${group3.joinToString(" ")}",
            "Chord: ${group4.joinToString(" ")}",
            "Chord: ${group5.joinToString(" ")}",
            "Chord: ${group6.joinToString(" ")}",
            "Chord: ${group7.joinToString(" ")}",
            "Chord: ${group0.joinToString(" ")}",
            "Chord: ${group0.joinToString(" ")}",
        ).joinToString("\n"), list.joinToString("\n"))

        assertEquals(list[0].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[1].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[2].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[3].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[4].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[5].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[6].cast<Chord>().arpeggio, ArpeggioType.None)
        assertEquals(list[7].cast<Chord>().arpeggio, ArpeggioType.Ascending)
        assertEquals(list[8].cast<Chord>().arpeggio, ArpeggioType.Downward)

        val numList = toInMusicScoreList(
                "6++:2:1 " +
                    "6:2--:1 " +
                    "6:2:1++ " +
                    "6%50:2%20:1%20 " +
                    "6:2\":1' " +
                    "6:m:m " +
                    "6:2↑:1↑ " +
                    "6:2:1↟ " +
                    "6:2:1↡ ",
            useMacro = false, isStave = false)

        assertEquals(list, numList)
    }

    @Test
    @DisplayName("testGlissando")
    fun testGlissando() {
        val list = toInMusicScoreList("c=d c≈d↑ c--=d++=e. #c=\$d=e' c%50=d%50=e%20", useMacro = false)

        val group = listOf(
            "Glissando: [60=C4|$duration|$velocity] [62=D4|$duration|$velocity]",
            "Glissando: [60=C4|$duration|$velocity] [74=D5|$duration|$velocity]",
            "Glissando: [60=C4|${duration/4}|$velocity] [62=D4|${duration*4}|$velocity] [64=E4|${duration*1.5}|$velocity]",
            "Glissando: [61=#C4|$duration|$velocity] [61=#C4|$duration|$velocity] [63=#D4|$duration|$velocity]",
            "Glissando: [60=C4|$duration|${velocity/2}] [62=D4|$duration|${velocity/2}] [64=E4|$duration|${velocity/5}]"
        )

        assertEquals(group.joinToString("\n"), list.joinToString("\n"))
        assert(!list[0].cast<Glissando>().isWave)
        assert(list[1].cast<Glissando>().isWave)
        assert(!list[2].cast<Glissando>().isWave)
        assert(!list[3].cast<Glissando>().isWave)
        assert(!list[4].cast<Glissando>().isWave)

        val numList = toInMusicScoreList("1=2 1≈2i 1--=2++=3. #1=b2=3' 1%50=2%50=3%20", useMacro = false, isStave = false)

        assertEquals(list, numList)
    }
}

class TestUtils {
    @Test
    fun testNextGivenChar() {
        val insert = "you"
        val string = "how did [$insert] mom?"
        val result = string.nextGivenChar(string.indexOf('['), ']', 3)
        assertEquals(insert, result)
    }

    @Test
    fun testNoteBaseOffset() {
        assertArrayEquals(arrayOf(0, 2, 4, 5, 7, 9, 11), arrayOf(
            noteBaseOffset("C"),
            noteBaseOffset("D"),
            noteBaseOffset("E"),
            noteBaseOffset("F"),
            noteBaseOffset("G"),
            noteBaseOffset("A"),
            noteBaseOffset("B")
        ))
    }

    @Test
    fun testNoteNameFromCode() {
        val list = mutableListOf<String>()
        for (i in 0..127) list += noteNameFromCode(i)

        assertEquals((listOf(
            "C", "#C", "D", "#D",
            "E", "F", "#F", "G",
            "#G", "A", "#A", "B"
        ) * 11).removeLastAndReturnSelf(4), list)
    }

    @Test
    fun testNoteNameFromCodeFlat() {
        val list = mutableListOf<String>()
        for (i in 0..127) list += noteNameFromCodeFlat(i)

        assertEquals((listOf(
            "C", "bD", "D", "bE",
            "E", "F", "bG", "G",
            "bA", "A", "bB", "B"
        ) * 11).removeLastAndReturnSelf(4), list)
    }

    @Test
    fun testGlissandoPoints() {
        assertEquals("[([60=C4|0.25|100], [64=E4|0.25|100]), ([64=E4|0.25|100], [67=G4|0.25|100]), ([67=G4|0.25|100], [71=B4|0.25|100])]",
            listOf(Note("C"),
                Note("E"),
                Note("G"),
                Note("B")
            ).glissandoPoints().toString())
    }
}

class TestMiderDsl : ABTestInMusicScore() {
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
            generate("G"),
            generate("C")
        ).jts(), dsl.jts())

        assert(dsl[-2].cast<Note>().attach?.lyric == "ai2")
        assert(dsl.last().cast<Note>().isNature)
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

        assert(dsl.container.mainList.last().cast<Glissando>().isWave)
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

        assert(!dsl.container.mainList.last().cast<Appoggiatura>().isFront)
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

abstract class ABTestInMusicScore {
    protected val duration: Double = 0.25
    protected val velocity: Int = 100
    protected fun generate(name: String, duration: Double = this.duration, pitch: Int = 4, velocity: Int = this.velocity): String = generateNoteString(name, duration, pitch, velocity)

    protected fun List<String>.generate(separator: String = "\n", pitch: Int = 4, duration: Double = this@ABTestInMusicScore.duration, velocity: Int = this@ABTestInMusicScore.velocity): String {
        val mutableList = mutableListOf<String>()
        forEach {
            mutableList += generate(it, duration, pitch = pitch, velocity = velocity)
        }
        return mutableList.jts(separator)
    }

    protected fun List<*>.jts(separator: String = "\n"): String = joinToString(separator)
    protected fun InMusicScoreContainer.jts(separator: String = "\n") = mainList.jts(separator)
    protected fun MiderDSL.jts(separator: String = "\n") = container.jts(separator)
    protected fun MiderDSL.last() = container.mainList.last()
    protected operator fun MiderDSL.get(index: Int): InMusicScore {
        return if (index >= 0)
            container.mainList[index]
        else
            container.mainList[container.mainList.size + index]
    }
}


