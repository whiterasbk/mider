package whiter.music.mider

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import whiter.music.mider.descr.InMusicScoreEvent

internal class HexUtilsKtTest {

    @Test
    fun parseToMidiHex() {
        val tArr0 = InMusicScoreEvent("onC,0,64,3").getHex(960 * 2)
        val bytes0 = arrayOf(0, 0x93, 72, 64).asByteArray()
        assertArrayEquals(bytes0, tArr0)

        val tArr1 = InMusicScoreEvent("onC").getHex(960 * 2)
        val bytes1 = arrayOf(0, 0x90, 72, 0x64).asByteArray()
        assertArrayEquals(bytes1, tArr1)

        val tArr2 = InMusicScoreEvent("off c").getHex(960 * 2)
        val bytes2 = arrayOf(0x83, 0x60, 0x80, 60, 100).asByteArray()
        assertArrayEquals(bytes2, tArr2)

        val tArr3 = InMusicScoreEvent("0 10 ad dc").getHex(960 * 2)
        val bytes3 = arrayOf(0, 0x10, 0xad, 0xdc).asByteArray()
        assertArrayEquals(bytes3, tArr3)

        val tArr4 = InMusicScoreEvent("3490da").getHex(960 * 2)
        val bytes4 = arrayOf(0x34, 0x90, 0xda).asByteArray()
        assertArrayEquals(bytes4, tArr4)

        val tArr5 = InMusicScoreEvent("i7=musicbox, 100").getHex(960 * 2)
        val bytes5 = arrayOf(0x64, 0xc7, 0x0b).asByteArray()
        assertArrayEquals(bytes5, tArr5)

        val tArr6 = InMusicScoreEvent("ca=1,3, 100").getHex(960 * 2)
        val bytes6 = arrayOf(0x64, 0xba, 0x1, 0x3).asByteArray()
        assertArrayEquals(bytes6, tArr6)

        val tArr7 = InMusicScoreEvent("c=7,255").getHex(960 * 2)
        val bytes7 = arrayOf(0, 0xb0, 0x7, 0xff).asByteArray()
        assertArrayEquals(bytes7, tArr7)

        val tArr8 = InMusicScoreEvent("off c/5..").getHex(960 * 2)
        val deltaTime = (960 * 2 / 4 / 5 * 1.5 * 1.5).toInt().asvlByteArray().map { it.toInt() }.toTypedArray()
        val bytes8 = arrayOf(*deltaTime, 0x80, 60, 0x64).asByteArray()
        assertArrayEquals(bytes8, tArr8)
    }
}