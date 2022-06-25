import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import whiter.music.mider.cast
import whiter.music.mider.code.toInMusicScoreList
import whiter.music.mider.descr.Appoggiatura
import whiter.music.mider.descr.ArpeggioType
import whiter.music.mider.descr.Chord
import whiter.music.mider.descr.Note
import whiter.music.mider.nextGivenChar
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

class TestMiderCodeParser {
    @Test
    @DisplayName("testNormal")
    fun testNormal() {
        var list = toInMusicScoreList("cdefgabCDEFGAB", useMacro = false)
        val duration = 0.25
        val velocity = 100
        val expected = listOf(
            "[60=C4|$duration|$velocity]",
            "[62=D4|$duration|$velocity]",
            "[64=E4|$duration|$velocity]",
            "[65=F4|$duration|$velocity]",
            "[67=G4|$duration|$velocity]",
            "[69=A4|$duration|$velocity]",
            "[71=B4|$duration|$velocity]",
            "[72=C5|$duration|$velocity]",
            "[74=D5|$duration|$velocity]",
            "[76=E5|$duration|$velocity]",
            "[77=F5|$duration|$velocity]",
            "[79=G5|$duration|$velocity]",
            "[81=A5|$duration|$velocity]",
            "[83=B5|$duration|$velocity]"
        ).joinToString("\n")

        assertEquals(expected, list.joinToString("\n"))

        list = toInMusicScoreList("1234567 1i2i3i4i5i6↑7↑", isStave = false, useMacro = false)

        assertEquals(expected, list.joinToString("\n"))
    }

    @Test
    @DisplayName("testFlatAndSharp")
    fun testFlatAndSharp() {
        var list = toInMusicScoreList("#c\$d♭e♮fga\"b'", useMacro = false)
        val duration = 0.25
        val velocity = 100
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

        list = toInMusicScoreList("#1$2b3&45♯67'", isStave = false, useMacro = false)

        assertEquals(expected, list.joinToString("\n"))
        assert(list[3].cast<Note>().isNature)
    }

    @Test
    @DisplayName("testAppoggiatura")
    fun testAppoggiatura() {
        val list = toInMusicScoreList("a;d++ a++;d a%50;d%20 a;dt++", useMacro = false)
        val duration = 0.25
        val velocity = 100

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
    }

    @Test
    @DisplayName("testChord")
    fun testChord() {
        var list = toInMusicScoreList(
            "a++:d:c " +
                "a:d:c++ " +
                "a%50:d%20:c%20 " +
                "a:d\":c' " +
                "a:m:m " +
                "a:d↑:c↑ " +
                "a:d:c↟" +
                "a:d:c↡",
            useMacro = false)
        val duration = 0.25
        val velocity = 100

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
        
        fun assert() {
            assertEquals(listOf(
                "Chord: ${group1.joinToString(" ")}",
                "Chord: ${group1.joinToString(" ")}",
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
            assertEquals(list[6].cast<Chord>().arpeggio, ArpeggioType.Ascending)
            assertEquals(list[7].cast<Chord>().arpeggio, ArpeggioType.Downward)
        }

        assert()
        list = toInMusicScoreList(
                "6++:2:1 " +
                    "6:2:1++ " +
                    "6%50:2%20:1%20 " +
                    "6:2\":1' " +
                    "6:m:m " +
                    "6:2↑:1↑ " +
                    "6:2:1↟" +
                    "6:2:1↡",
            useMacro = false, isStave = false)
        
        assert()
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
}

