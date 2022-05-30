import whiter.music.mider.code.miderCodeToMiderDSL
import whiter.music.mider.dsl.playDslInstance

fun main(args: Array<String>) {
    val miderDSL = miderCodeToMiderDSL(">g>1155665  4433221  5544332  5544332")
    playDslInstance(miderDSL = miderDSL)
}