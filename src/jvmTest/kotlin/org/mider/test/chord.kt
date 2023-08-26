
package org.mider.test

fun main() {
    applyInTestResDir("canon chord.mid") {

        duration = 1.0/2 // 默认为二分音符
        pitch = 3

        repeat(10) {
            C triad majorChord inverse G // C大三和弦的第二转位
            G triad majorChord
            A-1 triad majorChord inverse E
            E triad majorChord
            F triad majorChord
            C triad majorChord inverse E
            F triad majorChord
            G triad majorChord
        }
    }
}