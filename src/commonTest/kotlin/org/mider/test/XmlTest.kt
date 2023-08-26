package org.mider.test

import io.whiterasbk.kotlin.slowxml.parseXml
import org.mider.xml.AttributesElement
import org.mider.xml.MusicXml
import org.mider.xml.Node
import org.mider.xml.NoteElement
import kotlin.test.Test
import kotlin.test.assertEquals

class XmlTest {
    @Test
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

        assertEquals(parseXml(expected), parseXml(attr.toString()))
    }

    @Test
    fun testNotes() {

        val expected = """
        <note>
            <pitch>
                <step>E</step>
                <alter>-1</alter>
                <octave>5</octave>
            </pitch>
            <duration>24</duration>
            <tie type="start"></tie>
            <type>whole</type>
            <lyric>
                <syllabic>single</syllabic>
                <text>meil</text>
                <extend></extend>
            </lyric>
        </note>
        """.trimLines()

        val note = NoteElement("E", 5, 24, -1)
        note.addTie("start")
        note.addType()
        note.addLyric("meil")

        assertEquals(parseXml(expected), parseXml(note.toString()))
    }

    @Test
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

        assertEquals(parseXml(expected), parseXml(musicXml.toString()))
    }
}