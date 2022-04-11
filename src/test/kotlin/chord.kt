import whiter.music.mider.dsl.apply

fun main(args: Array<String>) {
    apply("src/test/resources/canon chord.mid") {

        defaultNoteDuration = 2 // 默认为二分音符
        pitch = 3

        C triad major inverse G // C大三和弦的第二转位
        G triad major
        A-1 triad minor inverse C
        E triad minor
        F triad major
        C triad major inverse E
        F triad major
        G triad major
    }
}