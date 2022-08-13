package whiter.music.mider.xml

import java.io.File
import kotlin.properties.Delegates

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

/**
 * @param perMinute tempo, 每分钟多少拍
 * @param beatUnit 时间单位
 */
class DirectionElement(perMinute: Int = 80, beatUnit: DurationType = DurationType.quarter) : DeepNode("direction") {
    init {
        this += Node("direction-type",
            Node("metronome",
                Node("beat-unit", beatUnit.name),
                Node("per-minute", perMinute)
            )
        )
        this += Node("sound", "tempo" to perMinute)
    }
}

class PitchElement(name: String, octave: Int, alter: Int? = null) : DeepNode("pitch") {
    init {
        this += Node("step", name)
        alter?.let {
            this += Node("alter", it)
        }
        this += Node("octave", octave)
    }
}

class LyricElement(text: String) : DeepNode("lyric") {
    init {
        addSyllabic("single")
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
    whole,
    half,
    quarter,
    eighth,
    `16th`, /*sixteenth*/
    `32th`, /*demisemiquaver*/
    `64th`, /*hemidemisemiquaver*/
    `128th` /*hemidemisemiquaver*/
}

class NoteElement : DeepNode {

    var duration by Delegates.notNull<Int>()

    constructor(duration: Int) : super("note") {
        this += Node("duration", duration)
        this.duration = duration
    }

    constructor(name: String, octave: Int, duration: Int, alter: Int? = null) : super("note") {
        addPitch(name, octave, alter)
        this += Node("duration", duration)
        this.duration = duration
    }

    fun addPitch(name: String, octave: Int, alter: Int? = null): NoteElement {
        this += PitchElement(name, octave, alter)
        return this
    }

    fun addDot(): NoteElement {
        this += Node("dot")
        return this
    }

    fun setRest() : NoteElement {
        this += Node("rest")
        return this
    }

    fun setChord() : NoteElement {
        this +=  Node("chord")
        return this
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

    fun addNotation(notation: NotationElement): NoteElement {
        this += notation
        return this
    }

    fun addType(type: DurationType = DurationType.whole): NoteElement {
        this += Node("type", type.name)
        return this
    }

    fun addType(typeName: String): NoteElement {
        this += Node("type", typeName)
        return this
    }
}

class NotationElement : DeepNode("notations") {
    fun addTied(typeName: String): NotationElement {
        this += Node("tied", "type" to typeName)
        return this
    }

    fun addSlur(typeName: String): NotationElement {
        this += Node("slur", "type" to typeName)
        return this
    }

    fun addArpeggiate(typeName: String): NotationElement {
        this += Node("arpeggiate", "type" to typeName)
        return this
    }
}

class MeasureElement(number: Int) : DeepNode("measure") {
    init {
        attributes["number"] = number
    }

//    fun addNote(note: NoteElement): MeasureElement {
//        this += note
//        return this
//    }
//
//    fun addAttributes(attr: AttributesElement): MeasureElement {
//        this += attr
//        return this
//    }

    fun add(node: Node): MeasureElement {
        this += node
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
                measure.add(note)
            }

            fun attr(attr: AttributesElement) {
                measure.add(attr)
            }

            fun direction(direction: DirectionElement) {
                measure.add(direction)
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

