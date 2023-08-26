package org.mider.test

import kotlin.math.PI

fun main() {
    applyInTestResDir("pi.mid") {
        PI.toString()(isStave = false)
    }
}