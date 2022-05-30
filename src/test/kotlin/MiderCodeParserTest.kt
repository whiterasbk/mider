import whiter.music.mider.code.miderCodeToMiderDSL
import whiter.music.mider.dsl.playDslInstance
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
fun main(args: Array<String>) {
    val miderDSL = miderCodeToMiderDSL(">g>A#F-G-A#F-G-A-a-^#C-D-^#F-G- #FD-E-#F #f-g-^^vv^#f-g-^ gb-vg#f-e-#f-e-d-e-#f-g-^^ gb-v b#C-D- a-^#C-D-^#F-G-^")
    playDslInstance(miderDSL = miderDSL)
}