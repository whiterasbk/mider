
package org.mider.test

import org.mider.dsl.apply


fun main(args: Array<String>) {
    apply("src/jvmTest/resources/canon chord.mid") {

        duration = 1.0/2 // 默认为二分音符
        pitch = 3


        C triad majorChord inverse G // C大三和弦的第二转位
        G triad majorChord
        A-1 triad majorChord inverse C
        E triad majorChord
        F triad majorChord
        C triad majorChord inverse E
        F triad majorChord
        G triad majorChord
    }
}