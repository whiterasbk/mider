## mider

[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![Kotlin](https://img.shields.io/badge/kotlin-100%25-blue)
![Use](https://img.shields.io/badge/mid-generate-yellowgreen)
![Rubbish](https://img.shields.io/badge/%E6%B2%BB%E7%96%97-%E4%BD%8E%E8%A1%80%E5%8E%8B-orange)
[![](https://www.jitpack.io/v/whiterasbk/mider.svg)](https://www.jitpack.io/#whiterasbk/mider)


[comment]: <> ([![Language: Kotlin]&#40;https://img.shields.io/github/languages/top/shadowsocks/shadowsocks-android.svg&#41;]&#40;https://https://github.com/whiterasbk/mider/search?l=kotlin&#41;)

[comment]: <> ([![Releases]&#40;https://img.shields.io/github/downloads/shadowsocks/shadowsocks-android/total.svg&#41;]&#40;https://github.com/shadowsocks/shadowsocks-android/releases&#41;)

《论要是不知道有 `javax.sound.midi` 包的存在会发生什么》

人类低质量`.mid`文件生成库, 让你体验写三行代码不如别的库写一行代码的感觉

纯`kotlin`实现, 效率低下, 强迫症暴怒, 低血压良方

名称抄袭自 [mido](https://github.com/mido/mido)

### 安装
使用`gradle`

在`build.gradle`添加如下代码

```groovy
repositories {
    mavenCentral()
    maven { url 'https://www.jitpack.io' }
}

dependencies {
    implementation "com.github.whiterasbk:mider:beta0.9.3"
}
```
然后就可以使用啦

### 简单使用

```kotlin
import whiter.music.mider.MidiFile
import whiter.music.mider.MetaEventType.*
import whiter.music.mider.EventType.*
import whiter.music.mider.*
import whiter.music.mider.Note.*

fun main(vararg args: String) {
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
}
```
#### mider-dsl
`mider`基于`kotlin`设计了一套易用的, 形式上更简洁的`dsl`框架, 该框架的优点是简单但功能强大(指提升血压)~~并且反人体工学设计~~

要开始使用, 只需要

```kotlin
import whiter.music.mider.dsl.play

fun main(vararg args: String) {
    play {
        repeat { C; D; E; C } // 重复两次 
        repeat { E; F; G * 2 } // 音名 * [/] 数字 是调节时值
        repeat { '8' { G; A; G; F }; E; C } // 表示在作用范围内, 一个音符的默认时值为八分音符
        repeat { C; G - 1; C * 2 } // 音名 + [-] 数字 是升高或者降低一个八度
    }
}
```

你已经学会`mider-dsl`的基本用法啦, 赶快去写一首野蜂飞舞吧(

```kotlin
import whiter.music.mider.dsl.play

fun main(vararg args: String) {
    play {
        E(minor) {

            pitch = 5
            bpm = 120

            val a0 = def { G; E; G } // 定义成 def 函数以便复用, 得到的复用对象可以通过!实现复用
            val a1 = def { A * 2.dot; G / 2; F / 2 } // dot 是加附点
            val a2 = def { G * 2.dot; A / 2; G / 2 }
            val a3 = def { F; E; D; F }
            val a4 = def { F * 2.dot; A / 2; G / 2 }

            val p1 = def { !a0; B; !a1 }

            repeat {
                !p1
                !a2; replace({ !a3 }, { A; C + 1; B; F })
            }

            val p2 = exec { !a0; D + 1; !a1 } // 等同于 def 但是先执行一遍再返回复用对象
            val p3 = exec { !a4; D * 2 + 1; B; A } // +-*/可以结合使用, 但是要注意优先级问题
            val p4 = exec { G; B - 1; E; B; !a1 }
            val p5 = exec { !a4; F; C + 1; B; F }

            !p1
            !a2; !a3
            !p1
            !p5
            !p2
            !p3
            !p4
            !p5

            val a5 = def { E; C + 1 }
            '2' {
                3 * G; F // 数字 * 音名表示将这个音符重复指定次数
                2 * E; G; F / 2; G / 2

                2 * A; B; C + 1
                2 * B; A;
                val a6 = exec { A / 2; G / 2 }

                3 * G; A
                !a5; B; !a6
                B; A; D + 1; C / 2 + 1; B / 2
            }
            B * 2; C + 1; repeat { B; A }; G
            E * 2; !a5; B * 2; G; A
            val A2d = exec { A * 2.dot }; F;
            val a7 = exec { G; A; B }; C + 1
            val p6 = exec { B * 2.dot; G; F * 2; G; A }
            !A2d; 2 * A; G; F; G
            E * 2.dot; !a5; B; A; G
            !A2d; 2 * F; !a7
            !p6

            '2' {
                B; A; C + 1; B
            }
        }
    }
}
```

[![LvRfJK.png](https://s1.ax1x.com/2022/04/29/LvRfJK.png)](https://imgtu.com/i/LvRfJK)

~~嗯, 血压↑↑~~

生成 `mid` 可用 `apply`函数, 生成的文件可以在 [src/test/resources](https://github.com/whiterasbk/mider/tree/master/src/test/resources) 内查看

### 相对音准练习程序

一个训练相对音准的小`demo`, 音高范围目前在`C2~C3`

```kotlin
import whiter.music.mider.practise.absolutepitch.practise1

fun main(args: Array<String>) {
    practise1()
}
```

```shell
tips: 输入答案时可以用唱名, 也可以用音名或者音名加数字的是形式, 不区分大小写; 不写音高的时默认为2, 高八度的C必须要写成C3或8
输入训练次数: 5
第 1 道题, 请输入答案: 2
答案错误, 正确答案: E2

第 2 道题, 请输入答案: 7
答案正确: B2

第 3 道题, 请输入答案: 2
答案错误, 正确答案: E2

第 4 道题, 请输入答案: 4
答案错误, 正确答案: E2

第 5 道题, 请输入答案: 6
答案正确: A2

练习结束, 总共 5 道题, 正确 2 道, 正确率 40.0%
```
----------------
更多关于`mider dsl`的例子在 [src/test/kotlin](https://github.com/whiterasbk/mider/tree/master/src/test/kotlin) 目录下

注解和规范请参考 [mider-dsl]() 

~~点不开?那就对了, 因为我还没写~~

生成`mid`文件的核心部分没有引用来自`mider-dsl`部分的代码, 可以单独抽离使用. 嗯就这样.
