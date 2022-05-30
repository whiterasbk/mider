import whiter.music.mider.code.MacroConfiguration
import whiter.music.mider.code.MiderCodeParseCondition
import whiter.music.mider.code.miderCodeToMiderDSL
import whiter.music.mider.dsl.MiderDSL
import whiter.music.mider.dsl.play

fun main(args: Array<String>) {
    val dsl = miderCodeToMiderDSL(">g>1155665  4433221  5544332  5544332", MiderCodeParseCondition(), MacroConfiguration())
    play(block = dsl)
}