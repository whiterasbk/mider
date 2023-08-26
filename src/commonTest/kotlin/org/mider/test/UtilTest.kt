package org.mider.test

import org.mider.*
import org.mider.descr.Note
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {
    @Test
    fun testNextGivenChar() {
        val insert = "you"
        val string = "how did [$insert] mom?"
        val result = string.nextGivenChar(string.indexOf('['), ']', 3)
        assertEquals(insert, result)
    }

    @Test
    fun testNoteBaseOffset() {
        assertArrayEquals(arrayOf(0, 2, 4, 5, 7, 9, 11), arrayOf(
            noteBaseOffset("C"),
            noteBaseOffset("D"),
            noteBaseOffset("E"),
            noteBaseOffset("F"),
            noteBaseOffset("G"),
            noteBaseOffset("A"),
            noteBaseOffset("B")
        ))
    }

    @Test
    fun testNoteNameFromCode() {
        val list = mutableListOf<String>()
        for (i in 0..127) list += noteNameFromCode(i)

        assertEquals((listOf(
            "C", "#C", "D", "#D",
            "E", "F", "#F", "G",
            "#G", "A", "#A", "B"
        ) * 11).removeLastAndReturnSelf(4), list)
    }

    @Test
    fun testNoteNameFromCodeFlat() {
        val list = mutableListOf<String>()
        for (i in 0..127) list += noteNameFromCodeFlat(i)

        assertEquals((listOf(
            "C", "bD", "D", "bE",
            "E", "F", "bG", "G",
            "bA", "A", "bB", "B"
        ) * 11).removeLastAndReturnSelf(4), list)
    }

    @Test
    fun testGlissandoPoints() {
        assertEquals("[([60=C4|0.25|100], [64=E4|0.25|100]), ([64=E4|0.25|100], [67=G4|0.25|100]), ([67=G4|0.25|100], [71=B4|0.25|100])]",
            listOf(
                Note("C"),
                Note("E"),
                Note("G"),
                Note("B")
            ).glissandoPoints().toString())
    }
}