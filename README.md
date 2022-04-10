## mider

人类低质量`.mid`文件生成库, 让你体验写三行代码不如别的库写一行代码的感觉

名称抄袭自 [mido](https://github.com/mido/mido)

### 简单使用

```kotlin
import whiter.music.mider.MidiFile
import whiter.music.mider.MetaEventType.*
import whiter.music.mider.EventType.*
import whiter.music.mider.*
import whiter.music.mider.Note.*

val midi = MidiFile()
midi.append {
    track {
        tempo(120) // 设置 bpm 为 120
        end()
    }

    track {
        changeProgram(0) // 切换乐器为钢琴 
        noteOn(C4, 0) // 音符 C4 开始按下
        noteOff(C4, (1920 * .5).toInt()) // 音符 C4 演奏结束, 持续时间是二分音符的时值
        end()
    }
}

midi.save("path/to/save")
```
#### dsl
`mider`基于`kotlin`设计了一套易用的, 形式上更简洁的`dsl`框架, 该框架的优点是简单但功能强大~~并且反人体工学设计~~

要开始使用, 只需要
```kotlin
import whiter.music.mider.dsl.apply

apply("path/to/save.mid") {
    
}
```

更多关于`mider dsl`的注解和规范请参考[mider-dsl]() 

~~点不开?那就对了, 因为我还没写~~