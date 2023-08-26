package org.mider.test

import org.mider.dsl.apply
import kotlin.math.PI

fun main(args: Array<String>) {
    apply("src/test/resources/pi.mid") {
        +PI.toString()
    }
}