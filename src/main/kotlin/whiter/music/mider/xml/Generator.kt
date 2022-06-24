package whiter.music.mider.xml

import java.io.File

val xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
val xmlDocType = "<!DOCTYPE score-partwise PUBLIC\n" +
        "    \"-//Recordare//DTD MusicXML 3.0 Partwise//EC\"\n" +
        "    \"http://www.musicxml.org/dtds/partwise.dtd\">"

/**
 * music xml 根节点
 */
class ScorePartWiseElement(version: String = "3.0") : DeepNode("score-partwise") {
    var partsCount: Int = 1
    val partList = Node("part-list")

    init {
        attributes["version"] = version
        this += partList
    }

    inline fun addPart(block: (String) -> Node) {
        val id = "P" + (partsCount++)
        partList += Node("score-part", mutableMapOf("id" to id),
            Node("part-name", "Music")
        )

        this += block(id)
    }
}

class AttributesElement : DeepNode("attributes") {

    fun addDefault(): AttributesElement {
        return addDivisions().addKeySignature().addTimeSignature().addClef()
    }

    fun addDivisions(divisions: Int = 1) : AttributesElement {
        this += Node("divisions", divisions)
        return this
    }

    fun addKeySignature(fifths: Int = 0, mode: String = "major") : AttributesElement{
        this += Node("key",
            Node("fifths", fifths),
            Node("mode", mode)
        )
        return this
    }

    fun addTimeSignature(beats: Int = 4, beatType: Int = 4) : AttributesElement{
        this += Node("time",
            Node("beats", beats),
            Node("beat-type", beatType)
        )
        return this
    }

    fun addClef(sign: String = "G", line: Int = 2): AttributesElement {
        this += Node("clef",
            Node("sign", sign),
            Node("line", line)
        )

        return this
    }
}

class PitchElement(name: String, octave: Int, alter: Int? = null) : DeepNode("pitch") {
    init {
        this += Node("step", name)
        this += Node("octave", octave)
        alter?.let {
            this += Node("alter", it)
        }
    }
}

class LyricElement(text: String) : DeepNode("lyric") {
    init {
        addSyllabic("end")
        this += Node("text", text)
        addExtend()
    }

    fun addSyllabic(syllabic: String) {
        this += Node("syllabic", syllabic)
    }

    fun addExtend() {
        this += Node("extend")
    }
}

enum class DurationType {
    whole, half, quater, eighth, sixteenth, demisemiquaver, hemidemisemiquaver
}

class NoteElement(name: String, octave: Int, duration: Int, alter: Int? = null) : DeepNode("note") {

    init {
        this += PitchElement(name, octave, alter)
        this += Node("duration", duration)
    }

    fun addLyric(lyric: LyricElement) : NoteElement {
        this += lyric
        return this
    }

    fun addLyric(lyric: String): NoteElement {
        return addLyric(LyricElement(lyric))
    }

    fun addTie(type: String = "start"): NoteElement {
        this += Node("tie", "type" to type)
        return this
    }

    fun addType(type: DurationType = DurationType.whole): NoteElement {
        this += Node("type", type.name)
        return this
    }
}

class MeasureElement(number: Int) : DeepNode("measure") {
    init {
        attributes["number"] = number
    }

    fun addNote(note: NoteElement): MeasureElement {
        this += note
        return this
    }

    fun addAttributes(attr: AttributesElement): MeasureElement {
        this += attr
        return this
    }
}

class PartElement(id: String) : DeepNode("part") {
    var numberCount = 1

    init {
        attributes["id"] = id
    }

    inline fun addMeasure(block: (Int) -> Node): PartElement {
        this += block(numberCount++)
        return this
    }
}

class MusicXml(private val doctype: Boolean = true) {
    private val root = ScorePartWiseElement()

    class Part(val part: PartElement) {

        class Measure(val measure: MeasureElement) {
            fun note(note: NoteElement) {
                measure.addNote(note)
            }

            fun attr(attr: AttributesElement) {
                measure.addAttributes(attr)
            }
        }

        fun measure(block: Measure.() -> Unit) {
            part.addMeasure { number ->
                val measure = MeasureElement(number)
                Measure(measure).block()
                measure
            }
        }
    }

    fun part(block: Part.() -> Unit): MusicXml {
        root.addPart { id ->
            val part = PartElement(id)
            Part(part).block()
            part
        }

        return this
    }

    fun save(path: String) = save(File(path))

    fun save(file: File) {
        file.writeText(toString())
    }

    override fun toString(): String = xmlHead + (if (doctype) xmlDocType else "") + "\n" + root.toString()
}

