import whiter.music.mider.dsl.apply
import kotlin.math.PI

fun main(args: Array<String>) {
    apply("src/test/resources/pi.mid") {
        !PI
    }
}