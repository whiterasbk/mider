package org.mider.test

import org.mider.descr.InMusicScore
import org.mider.dsl.InMusicScoreContainer
import org.mider.dsl.MiderDSL

open class RunningTestInMusicScore {
    protected val duration: Double = 0.25
    protected val velocity: Int = 100
    protected fun generate(name: String, duration: Double = this.duration, pitch: Int = 4, velocity: Int = this.velocity): String = generateNoteString(name, duration, pitch, velocity)

    protected fun List<String>.generate(separator: String = "\n", pitch: Int = 4, duration: Double = this@RunningTestInMusicScore.duration, velocity: Int = this@RunningTestInMusicScore.velocity): String {
        val mutableList = mutableListOf<String>()
        forEach {
            mutableList += generate(it, duration, pitch = pitch, velocity = velocity)
        }
        return mutableList.jts(separator)
    }

    protected fun List<*>.jts(separator: String = "\n"): String = joinToString(separator)
    protected fun InMusicScoreContainer.jts(separator: String = "\n") = mainList.jts(separator)
    protected fun MiderDSL.jts(separator: String = "\n") = container.jts(separator)
    protected fun MiderDSL.last() = container.mainList.last()
    protected operator fun MiderDSL.get(index: Int): InMusicScore {
        return if (index >= 0)
            container.mainList[index]
        else
            container.mainList[container.mainList.size + index]
    }
}