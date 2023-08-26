package org.mider.test

import org.mider.xml.AttributesElement
import org.mider.xml.MusicXml
import org.mider.xml.NoteElement
import org.mider.xml.save
import java.io.File
import kotlin.test.Test

class XmlTestJvm {
    @Test
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