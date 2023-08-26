package org.mider.test

import org.mider.dsl.apply
import org.mider.dsl.play

fun main(vararg args: String) {
    apply("src/test/resources/oyasumi.mid") {
        +"59833598225981112"
    }

    play {
        +"#115566#5 1*2*7 45222↑ 66!6i"
    }
}