package org.mider.test

import org.junit.jupiter.api.Test
import org.mider.MidiFile
import java.io.File

class TestMidiFile {
    @Test
    fun `place holder`() {}
}

fun main() {
    val f = MidiFile()
    f.append {
        track {
            tempo(120)
            end()
        }

        track {
            noteOn(48, 920)
            noteOff(48, 0)
            end()
        }
    }


    File(testResourcesDir, "test.mid").writeBytes(
        ByteArray(f.getFileSize()).apply {
            f.doFinal().get(this, 0, f.getFileSize())
        })
}