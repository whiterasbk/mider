import org.mider.dsl.MiderDSL
import org.mider.dsl.fromDsl


@JsModule("midi-player-js")
@JsNonModule
external val MidiPlayer : dynamic

@JsModule("is-sorted")
@JsNonModule
external fun <T> sorted(a: Array<T>): Boolean

fun main() {
    console.log(123)

    console.log(sorted(arrayOf(234,123)))

    play {
        repeat(8) {
            A; B; C; D; E; F; G
        }
    }
}

fun play(block: MiderDSL.() -> Unit) {

    val file = fromDsl(block)
    val buffer = file.doFinal()
    val bytes = ByteArray(file.getFileSize())
    buffer.get(bytes, 0, file.getFileSize())

    println(bytes)

    val player = js("new MidiPlayer.default.Player()")
    println(MidiPlayer)
    println(MidiPlayer.default)
    println(MidiPlayer.default.Player)

    val l = js("new MidiPlayer.default.Player()")
    println(l.loadArrayBuffer)
    println(l.loadArrayBuffer(bytes))
    println(l.play)
    l.play()
//    player.loadArrayBuffer(bytes)
//    player.play()

//    println()
//    val player = MidiPlayer.Player { it ->
//        println(it)
//    }
//    js("for (var i in require('midi-player-js').default) console.log(i)")


//    var JZZ = js("require('jzz')")
//    val player = js("new JZZ.gui.Player('player')")
//    player.load(js("new JZZ.MIDI.SMF(bytes)"))
//    player.play()
//    js("var MidiPlayer = require('')")
//    js("var player = new MidiPlayer.Player()")
//    js("console.log(bytes)")
//    js("player.loadArrayBuffer(bytes)")
//    js("player.play()")


}