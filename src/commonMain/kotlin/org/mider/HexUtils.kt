package org.mider

import org.mider.impl.toByteArray
import kotlin.math.ln

//private val noteRegex = Regex("(on|off) *([b#]?[a-gA-G])(\\d?)(\\s*[, ]\\s*\\d+|[-+.~]+)?(\\s*[, ]\\s*\\d{1,3})?(\\s*[, ]\\s*\\d{1,2})?")
private val noteRegex = Regex("(on|off) *([b#]?[a-gA-G])(\\d?)(\\s*[, ]\\s*\\d+|([-+.~]|[/x]\\d)+)?(\\s*[, ]\\s*\\d{1,3})?(\\s*[, ]\\s*\\d{1,2})?")
private val instrumentRegex = Regex("i([a-fA-F]|\\d{1,2})?\\s*=\\s*(\\d{1,3}|[0-9a-zA-Z_ -]+)(\\s*[, ]\\s*\\d{1,3})?")
private val controllerRegex = Regex("c([a-fA-F]|\\d{1,2})?\\s*=\\s*\\d{1,3}\\s*,\\s*\\d{1,3}(\\s*[, ]\\s*\\d{1,3})?")
private val hexRegex = Regex("([0-9a-fA-F]{1,2} )*[0-9a-fA-F]{1,2}")
private val instanceHexRegex = Regex("[0-9a-fA-F]+")
private val titleRegex = Regex("title .+")
private val programNameRegex = Regex("instrumentName .+")
private val lyricRegex = Regex("lyric .+")

fun String.parseToMidiHex(wholeTick: Int, defaultOctave: Int = 4, defaultVelocity: Int = 100, defaultDuration: Int = 4, previousTicks: Int = 0, delimiter: String = " "): ByteArray = when {

    this matches hexRegex ->
        split(delimiter).map { byte -> byte.toInt(16).toByte() }.toByteArray()

    this matches instanceHexRegex -> {
        if (length % 2 != 0) throw Exception("length of given hex data is expected to be even.") else {
            val list = mutableListOf<String>()
            forEachIndexed { index, char ->
                if (index % 2 == 1) list += this[index - 1].toString() + char
            }
            list.map { byte -> byte.toInt(16).toByte() }.toByteArray()
        }
    }

    this matches noteRegex -> {

        val values = noteRegex.find(this)?.groupValues
        val operation = values?.get(1)
        val name = values?.get(2)
        val octave = values?.get(3)?.toIntOrNull() ?: run {
            name?.let {
                if (it.last() in 'a' .. 'g') defaultOctave
                else if (it.last() in 'A' .. 'G') defaultOctave + 1
                else throw Exception("no such note name: $it")
            }
        } ?: defaultOctave
        val timeDesc = values?.get(4)?.replace(",", "")?.trim()?.let {
            if (it matches Regex("([-+.~]|[/x](\\d))+")) {
                val lnWTDivDD = wholeTick / defaultDuration.toDouble()
                val intAbove = lnWTDivDD.toInt()
                // fix
                when (it) {
                    "." -> (intAbove * 1.5).toInt()
                    ".." -> (intAbove * 1.5 * 1.5).toInt()
                    "..." -> (intAbove * 1.5 * 1.5 * 1.5).toInt()
                    "/3" -> intAbove / 3
                    "/6" -> intAbove / 6
                    "/9" -> intAbove / 9
                    else -> it.replace("~", "")
                            .durationSymbolsToMultiple(ln(lnWTDivDD))
                            .toInt()
                }
            } else it.toIntOrNull()
        } ?: run {
            if (operation != null) {
                if (operation == "on") 0 else wholeTick / defaultDuration
            } else throw Exception("operation name is expected.")
        }
        val velocity = values?.get(6)?.replace(",", "")?.trim()?.toIntOrNull() ?: defaultVelocity
        val channel = values?.get(7)?.replace(",", "")?.trim()?.toIntOrNull() ?: 0
        if (channel !in 0..0xf) throw Exception("channel is expected in the range of 0~16, given: $channel")
        val code = name?.let {
            noteBaseOffset(it.last().uppercase()) + (octave + 1) * 12
        } ?: throw Exception("note name are expected.")

        byteArrayOf(
            *(previousTicks + timeDesc).asvlByteArray(),
            ((operation ?: throw Exception("match operation code failed.")).let { if (it == "on") 0x90 else 0x80 } or channel).toByte(),
            code.toByte(),
            velocity.toByte()
        )
    }

    this matches instrumentRegex -> {

        val values = instrumentRegex.find(this)?.groupValues
        val channel = values?.get(1)?.toIntOrNull(16) ?: 0
        val instrument = values?.get(2)?.toIntOrNull() ?: run {
            values?.get(2)?.let { MidiInstrument.valueOf(it).id }
        } ?: throw Exception("instrument must be provided.")
        val time = values?.get(3)?.replace(",", "")?.trim()?.toIntOrNull() ?: 0

        byteArrayOf(
            *(previousTicks + time).asvlByteArray(),
            (0xc0 or channel).toByte(),
            instrument.toByte()
        )
    }

    this matches controllerRegex -> {
        val data = replace(Regex("\\s*c.*\\s*=\\s*"), "").split(",").map { it.trim().toInt() }
        val number = data[0]
        val parameter = data[1]
        val values = controllerRegex.find(this)?.groupValues
        val channel = values?.get(1)?.toIntOrNull(16) ?: 0
        val time = values?.get(2)?.replace(",", "")?.trim()?.toIntOrNull() ?: 0
        byteArrayOf(
            *(previousTicks + time).asvlByteArray(),
            (0xb0 or channel).toByte(),
            number.toByte(),
            parameter.toByte()
        )
    }

    this matches titleRegex -> asMetaEventData("title ", 3, previousTicks)

    this matches programNameRegex -> asMetaEventData("instrumentName ", 4, previousTicks)

    this matches lyricRegex -> asMetaEventData("lyric ", 5, previousTicks)

    else -> throw Exception("this operation has not yet implement: $this")
}

private fun String.asMetaEventData(prefix: String, type: Int, previousTicks: Int = 0) = removePrefix(prefix).toByteArray().let {
    byteArrayOf(
        *previousTicks.asvlByteArray(), 0xff.toByte(), type.toByte(),
        *it.size.asvlByteArray(),
        *it
    )
}



