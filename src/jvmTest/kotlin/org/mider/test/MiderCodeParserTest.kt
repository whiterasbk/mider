package org.mider.test

import org.mider.code.produceCore
import org.mider.dsl.fromDslInstance
import org.mider.dsl.playDslInstance
import org.mider.save

fun main(args: Array<String>) {
    val result = produceCore(">190b;i=musicbox>A#F-G-A#F-G-A-a-^#C-D-^#F-G- #FD-E-#F #f-g-^^vv^#f-g-^ gb-vg#f-e-#f-e-d-e-#f-g-^^ gb-v b#C-D- a-^#C-D-^#F-G-^")
    result.logs.forEach{ println(it) }
    playDslInstance(miderDSL = result.miderDSL)
    fromDslInstance(result.miderDSL).save("src/test/resources/2filename.mid")
}