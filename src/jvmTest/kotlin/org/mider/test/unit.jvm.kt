package org.mider.test

import org.mider.dsl.play

fun main() {
    play {
        bpm = 240
        +"""
            A#F-G-A#F-G-A-a-^#C-D-^#F-G- #FD-E-#F #f-g-^^vv^#f-g-^ gb-vg#f-e-#f-e-d-e-#f-g-^^ gb-v b#C-D- a-^#C-D-^#F-G-^
        """.trimIndent()
    }
}
