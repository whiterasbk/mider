package org.mider.test

import net.sourceforge.lame.lowlevel.LameEncoder
import net.sourceforge.lame.mp3.Lame
import net.sourceforge.lame.mp3.MPEGMode
import org.junit.jupiter.api.Test
import org.mider.MidiInstrument
import org.mider.dsl.MiderDSL
import org.mider.dsl.fromDsl
import org.mider.dsl.play
import org.mider.dsl.playDslInstance
import org.mider.inStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class TestLame {
    @Test
    fun `place holder`() {

    }
}

fun main() {
    fun gMp3(noteName: String) {
        val block: MiderDSL.() -> Unit = {
            bpm = 120
            instrument(MidiInstrument.musicbox) {
                +noteName
            }
        }

        val mp3 = midi2mp3Stream(midiStream = fromDsl(block).inStream())
        play(block = block)
        File(testResourcesDir.path + "/musicbox/", "$noteName.mp3").writeBytes(mp3.readBytes())
    }

    val hash = "#"
    val list1 = listOf("C4", "${hash}C4", "D4", "${hash}D4", "E4", "F4", "${hash}F4", "G4", "${hash}G4", "A4", "${hash}A4", "B4", "C5")
    val list2 = listOf("C4", "D4", "E4", "F4", "G4", "A4", "B4", "C5")
    for (s in list1) {
        println("g: $s")
        gMp3(s)
    }
}

fun midi2mp3Stream(USE_VARIABLE_BITRATE: Boolean = false, GOOD_QUALITY_BITRATE: Int = 256, midiStream: InputStream): ByteArrayInputStream {
    val audioInputStream = AudioSystem.getAudioInputStream(midiStream)
    return wave2mp3Stream(audioInputStream, USE_VARIABLE_BITRATE, GOOD_QUALITY_BITRATE)
}
fun wave2mp3Stream(audioInputStream: AudioInputStream, USE_VARIABLE_BITRATE: Boolean = false, GOOD_QUALITY_BITRATE: Int = 256): ByteArrayInputStream {
    val encoder = LameEncoder(
        audioInputStream.format,
        GOOD_QUALITY_BITRATE,
        MPEGMode.STEREO,
        Lame.QUALITY_HIGHEST,
        USE_VARIABLE_BITRATE
    )

    val mp3 = ByteArrayOutputStream()
    val inputBuffer = ByteArray(encoder.pcmBufferSize)
    val outputBuffer = ByteArray(encoder.pcmBufferSize)
    var bytesRead: Int
    var bytesWritten: Int
    while (0 < audioInputStream.read(inputBuffer).also { bytesRead = it }) {
        bytesWritten = encoder.encodeBuffer(inputBuffer, 0, bytesRead, outputBuffer)
        mp3.write(outputBuffer, 0, bytesWritten)
    }

    encoder.close()
    return ByteArrayInputStream(mp3.toByteArray())
}