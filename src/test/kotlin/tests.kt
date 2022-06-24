import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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

