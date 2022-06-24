package whiter.music.mider

import kotlin.math.abs

/**
 * 所有 midi 音符的枚举
 */
enum class MidiNote(val id: Byte) {
    `C-1`(0), `CS-1`(1), `D-1`(2), `DS-1`(3), `E-1`(4), `F-1`(5), `FS-1`(6), `G-1`(7), `GS-1`(8), `A-1`(9), `AS-1`(10), `B-1`(11),
    C0(12), CS0(13), D0(14), DS0(15), E0(16), F0(17), FS0(18), G0(19), GS0(20), A0(21), AS0(22), B0(23),
    C1(24), CS1(25), D1(26), DS1(27), E1(28), F1(29), FS1(30), G1(31), GS1(32), A1(33), AS1(34), B1(35),
    C2(36), CS2(37), D2(38), DS2(39), E2(40), F2(41), FS2(42), G2(43), GS2(44), A2(45), AS2(46), B2(47),
    C3(48), CS3(49), D3(50), DS3(51), E3(52), F3(53), FS3(54), G3(55), GS3(56), A3(57), AS3(58), B3(59),
    C4(60), CS4(61), D4(62), DS4(63), E4(64), F4(65), FS4(66), G4(67), GS4(68), A4(69), AS4(70), B4(71),
    C5(72), CS5(73), D5(74), DS5(75), E5(76), F5(77), FS5(78), G5(79), GS5(80), A5(81), AS5(82), B5(83),
    C6(84), CS6(85), D6(86), DS6(87), E6(88), F6(89), FS6(90), G6(91), GS6(92), A6(93), AS6(94), B6(95),
    C7(96), CS7(97), D7(98), DS7(99), E7(100), F7(101), FS7(102), G7(103), GS7(104), A7(105), AS7(106), B7(107),
    C8(108), CS8(109), D8(110), DS8(111), E8(112), F8(113), FS8(114), G8(115), GS8(116), A8(117), AS8(118), B8(119),
    C9(120), CS9(121), D9(122), DS9(123), E9(124), F9(125), FS9(126), G9(127);

    operator fun plus(v: Byte): MidiNote {
        return from((id + v).toByte()) ?: throw Exception("can not find id: ${id + v} in class Note")
    }

    operator fun minus(v: Byte): MidiNote {
        return from((id - v).toByte()) ?: throw Exception("can not find id: ${id - v} in class Note")
    }

    companion object {
        fun from(id: Byte): MidiNote? {
            // todo 使用二分查找查找音符, kotlin的查找太特么原始了（
            return values().find {
                it.id == id
            }
        }
    }

    fun up(signature: String = "C"): MidiNote {
        return when(toString().replace("`", "")[0]) {
            'E', 'B' -> this + 1
            else -> this + 2
        }
    }

    fun up(times: Byte, signature: String = "C"): MidiNote {
        if (times == 0.toByte()) return this

        var res: MidiNote = this
        for (i in 0 until times)
            res = res.up(signature)
        return res
    }

    fun down(signature: String = "C"): MidiNote {
        return when(toString().replace("`", "")[0]) {
            'F', 'C' -> this - 1
            else -> this - 2
        }
    }

    fun down(times: Byte, signature: String = "C"): MidiNote {
        if (times == 0.toByte()) return this

        var res: MidiNote = this
        for (i in 0 until times)
            res = res.down(signature)
        return res
    }

    fun shift(n: Int): MidiNote {
        return if (n == 0) this else if (n > 0) {
            up(n.toByte())
        } else {
            down(abs(n).toByte())
        }
    }



    /**
     * get note using:
        var j = -1
        for (i in 0..127 step 12) {
            val p = if (j == -1) "`" else ""
            val m = "${p}C$j${p}(${i}), ${p}CS$j${p}(${i+1}), ${p}D$j${p}(${i+2}), ${p}DS$j${p}(${i+3}), ${p}E$j${p}(${i+4}), ${p}F$j${p}(${i+5}), ${p}FS$j${p}(${i+6}), ${p}G$j${p}(${i+7}), ${p}GS$j${p}(${i+8}), ${p}A$j${p}(${i+9}), ${p}AS$j${p}(${i+10}), ${p}B$j${p}(${i+11}), "
            println(m)
            j += 1
        }
     */
}
