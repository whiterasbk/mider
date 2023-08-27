import org.mider.dsl.MiderDSL
import org.mider.dsl.fromDsl


fun main() {
    console.log(123)
    play {
        repeat(20) {
            A; B; C; D; E; F; G
        }
    }
}

fun play(block: MiderDSL.() -> Unit) {

    val file = fromDsl(block)
    val buffer = file.doFinal()
    val bytes = ByteArray(file.getFileSize())
    buffer.get(bytes, 0, file.getFileSize())


//    var JZZ = js("require('jzz')")
//    val player = js("new JZZ.gui.Player('player')")
//    player.load(js("new JZZ.MIDI.SMF(bytes)"))
//    player.play()
    js("var MidiPlayer = require('midi-player-js')")
    js("var player = new MidiPlayer.Player()")
    js("console.log(bytes)")
    js("player.loadArrayBuffer(bytes)")
    js("player.play()")


}