package org.mider.test

import org.mider.xml.*
import org.mider.descr.*
import org.mider.dsl.*
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.mider.code.toInMusicScoreList
import java.io.File


//
//class TestMiderCodeParser : ABTestInMusicScore() {
//
//    @Test
//    @DisplayName("testNormal")
//    fun testNormal() {
//        val list = toInMusicScoreList("cdefgabCDEFGAB", useMacro = false)
//        val expected = listOf(
//            generate("C"),
//            generate("D"),
//            generate("E"),
//            generate("F"),
//            generate("G"),
//            generate("A"),
//            generate("B"),
//            generate("C5"),
//            generate("D5"),
//            generate("E5"),
//            generate("F5"),
//            generate("G5"),
//            generate("A5"),
//            generate("B5")
//        ).joinToString("\n")
//
//        assertEquals(expected, list.joinToString("\n"))
//
//        val numList = toInMusicScoreList("1234567 1i2i3i4i5i6↑7↑", iIsStave = false, useMacro = false)
//        assertEquals(list, numList)
//    }
//
//    @Test
//    @DisplayName("testDuration")
//    fun testDuration() {
//        val list = toInMusicScoreList("c+d-e. c/3", useMacro = false)
//
//        assertEquals(listOf(
//            generate("C", duration * 2),
//            generate("D", duration / 2),
//            generate("E", duration * 1.5),
//            generate("C", duration / 3)
//        ).joinToString("\n"), list.joinToString("\n"))
//
//        assertEquals(3.0, list[3].duration.denominator)
//
//        val numList = toInMusicScoreList("1+2-3. 1/3", useMacro = false, iIsStave = false)
//        assertEquals(list, numList)
//    }
//
//    @Test
//    @DisplayName("testFlatAndSharp")
//    fun testFlatAndSharp() {
//        val list = toInMusicScoreList("#c\$d♭e♮fga\"b'", useMacro = false)
//        val expected = listOf(
//            "[61=#C4|$duration|$velocity]",
//            "[61=#C4|$duration|$velocity]",
//            "[63=#D4|$duration|$velocity]",
//            "[65=F4|$duration|$velocity]",
//            "[67=G4|$duration|$velocity]",
//            "[70=#A4|$duration|$velocity]",
//            "[70=#A4|$duration|$velocity]"
//        ).jts()
//
//        assertEquals(expected, list.jts())
//        assert(list[3].cast<Note>().isNature)
//
//        val numList = toInMusicScoreList("#1$2b3@45♯67'", iIsStave = false, useMacro = false)
//
//        assertEquals(list, numList)
//    }
//
//    @Test
//    fun testTieNote() {
//        val list = toInMusicScoreList("#c&#c d-&a.&d..")
//        assertEquals(listOf(
//            generate("#C", duration*2),
//            generate("D", duration/2 + duration*1.5 + duration*1.5*1.5),
//        ).jts(), list.jts())
//    }
//
//    @Test
//    @DisplayName("testAppoggiatura")
//    fun testAppoggiatura() {
//        val list = toInMusicScoreList("a;d++ a++;d a%50;d%20 a;dt++", useMacro = false)
//
//        val group1 = listOf(
//            "[69=A4|${duration}|$velocity]",
//            "[62=D4|${duration*4}|$velocity]"
//        )
//
//        val group2 = listOf(
//            "[69=A4|${duration*4}|$velocity]",
//            "[62=D4|${duration}|$velocity]"
//        )
//
//        val group3 = listOf(
//            "[69=A4|${duration}|${velocity / 2}]",
//            "[62=D4|${duration}|${velocity / 5}]"
//        )
//
//        assertEquals(listOf(
//            "Appoggiatura: ${group1.joinToString(" ")}",
//            "Appoggiatura: ${group2.joinToString(" ")}",
//            "Appoggiatura: ${group3.joinToString(" ")}",
//            "Appoggiatura: ${group1.joinToString(" ")}",
//        ).joinToString("\n"), list.joinToString("\n"))
//
//        assert(list[0].cast<Appoggiatura>().isFront)
//        assert(list[1].cast<Appoggiatura>().isFront)
//        assert(list[2].cast<Appoggiatura>().isFront)
//        assert(!list[3].cast<Appoggiatura>().isFront)
//
//        val numList = toInMusicScoreList("6;2++ 6++;2 6%50;2%20 6;2t++", useMacro = false, iIsStave = false)
//        assertEquals(list, numList)
//    }
//
//    @Test
//    @DisplayName("testChord")
//    fun testChord() {
//        val list = toInMusicScoreList(
//            "a++:d:c" +
//                "a:d--:c" +
//                "a:d:c++" +
//                "a%50:d%20:c%20 " +
//                "a:d\":c' " +
//                "a:m:m " +
//                "a:d↑:c↑ " +
//                "a:d:c↟" +
//                "a:d:c↡",
//            useMacro = false)
//
//        val group0 = listOf(
//            "[69=A4|$duration|$velocity]",
//            "[62=D4|$duration|$velocity]",
//            "[60=C4|$duration|$velocity]"
//        )
//
//        val group1 = listOf(
//            "[69=A4|${duration*4}|$velocity]",
//            "[62=D4|${duration}|$velocity]",
//            "[60=C4|${duration}|$velocity]"
//        )
//
//        val group2 = listOf(
//            "[69=A4|${duration}|$velocity]",
//            "[62=D4|${duration/4}|$velocity]",
//            "[60=C4|${duration}|$velocity]"
//        )
//
//        val group3 = listOf(
//            "[69=A4|${duration}|$velocity]",
//            "[62=D4|${duration}|$velocity]",
//            "[60=C4|${duration*4}|$velocity]"
//        )
//
//        val group4 = listOf(
//            "[69=A4|$duration|${velocity / 2}]",
//            "[62=D4|$duration|${velocity / 5}]",
//            "[60=C4|$duration|${velocity / 5}]"
//        )
//
//        val group5 = listOf(
//            "[69=A4|$duration|$velocity]",
//            "[63=#D4|$duration|$velocity]",
//            "[59=B3|$duration|$velocity]"
//        )
//
//        val group6 = listOf(
//            "[69=A4|$duration|$velocity]",
//            "[72=C5|$duration|$velocity]",
//            "[76=E5|$duration|$velocity]"
//        )
//
//        val group7 = listOf(
//            "[69=A4|$duration|$velocity]",
//            "[74=D5|$duration|$velocity]",
//            "[72=C5|$duration|$velocity]"
//        )
//
//        assertEquals(listOf(
//            "Chord: ${group1.joinToString(" ")}",
//            "Chord: ${group2.joinToString(" ")}",
//            "Chord: ${group3.joinToString(" ")}",
//            "Chord: ${group4.joinToString(" ")}",
//            "Chord: ${group5.joinToString(" ")}",
//            "Chord: ${group6.joinToString(" ")}",
//            "Chord: ${group7.joinToString(" ")}",
//            "Chord: ${group0.joinToString(" ")}",
//            "Chord: ${group0.joinToString(" ")}",
//        ).joinToString("\n"), list.joinToString("\n"))
//
//        assertEquals(list[0].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[1].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[2].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[3].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[4].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[5].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[6].cast<Chord>().arpeggio, ArpeggioType.None)
//        assertEquals(list[7].cast<Chord>().arpeggio, ArpeggioType.Ascending)
//        assertEquals(list[8].cast<Chord>().arpeggio, ArpeggioType.Downward)
//
//        val numList = toInMusicScoreList(
//                "6++:2:1 " +
//                    "6:2--:1 " +
//                    "6:2:1++ " +
//                    "6%50:2%20:1%20 " +
//                    "6:2\":1' " +
//                    "6:m:m " +
//                    "6:2↑:1↑ " +
//                    "6:2:1↟ " +
//                    "6:2:1↡ ",
//            useMacro = false, iIsStave = false)
//
//        assertEquals(list, numList)
//    }
//
//    @Test
//    @DisplayName("testGlissando")
//    fun testGlissando() {
//        val list = toInMusicScoreList("c=d c≈d↑ c--=d++=e. #c=\$d=e' c%50=d%50=e%20", useMacro = false)
//
//        val group = listOf(
//            "Glissando: [60=C4|$duration|$velocity] [62=D4|$duration|$velocity]",
//            "Glissando: [60=C4|$duration|$velocity] [74=D5|$duration|$velocity]",
//            "Glissando: [60=C4|${duration/4}|$velocity] [62=D4|${duration*4}|$velocity] [64=E4|${duration*1.5}|$velocity]",
//            "Glissando: [61=#C4|$duration|$velocity] [61=#C4|$duration|$velocity] [63=#D4|$duration|$velocity]",
//            "Glissando: [60=C4|$duration|${velocity/2}] [62=D4|$duration|${velocity/2}] [64=E4|$duration|${velocity/5}]"
//        )
//
//        assertEquals(group.joinToString("\n"), list.joinToString("\n"))
//        assert(!list[0].cast<Glissando>().isWave)
//        assert(list[1].cast<Glissando>().isWave)
//        assert(!list[2].cast<Glissando>().isWave)
//        assert(!list[3].cast<Glissando>().isWave)
//        assert(!list[4].cast<Glissando>().isWave)
//
//        val numList = toInMusicScoreList("1=2 1≈2i 1--=2++=3. #1=b2=3' 1%50=2%50=3%20", useMacro = false, iIsStave = false)
//
//        assertEquals(list, numList)
//    }
//}




