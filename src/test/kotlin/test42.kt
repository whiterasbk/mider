import whiter.music.mider.*
import kotlin.math.*


fun main(args: Array<String>) {
    val mdsl = MDSL()
    var minimsTicks = 960
//    minimsTicks = 839
    val clock: Byte = 18

    with(mdsl) {
        timeSignature = 4 to 4
        keySignature(F, major)
        bpm = 120
        A B C D D


//        (C to A) {
            G; E*2+1; E+1; G; D*2+1; D+1; G; C*2+1; C*2+1; C*2+1; D*2+1

//        }
    }

    val midi = MidiFile()
    midi.append {
        track {
            meta(MetaEventType.META_TEMPO, args = bpm(mdsl.bpm))
            val sf = mdsl.keySignature.first
//            if (mdsl.signatureKey.first >= 0) mdsl.signatureKey.first.toByte()
            meta(MetaEventType.META_KEY_SIGNATURE, mdsl.keySignature.first.semitone.toByte(), mdsl.keySignature.second.toByte())
            meta(MetaEventType.META_TIME_SIGNATURE, mdsl.timeSignature.first.toByte(), log2(mdsl.timeSignature.second.toDouble()).toInt().toByte(), clock, 8)
            meta(MetaEventType.META_END_OF_TRACK)
        }

        track {
            message(EventType.program_change, mdsl.program.id)

            for (i in mdsl.list) {
                val note = i.code
                message(EventType.note_on, note, 0)
                message(EventType.note_off, note, (minimsTicks * 2 * i.duration).toInt())
            }

            meta(MetaEventType.META_END_OF_TRACK)
        }
    }

    midi.save("src/main/resources/mdsl.mid")
}

fun a() {
    with(MDSL()) {

        keySignature(D, major)

        (+D)(major) {
            C; D; E
        }

        (F)(major) {
            C; D; E
        }

        println(signatureKeysList)


//        (C..G) step 1// major
//        (C to D) {
//            C D E F G A B
//        }


//        var n = arrayOf(2,2,1,2,2,2,1)
//        var c = 0
//        for (i in 0..32) {
//            var p = 0
//            p+=n[c % n.size]
//            println("$p, ${n[c % n.size]}")
//            c++
//        }

//        var from = 24
//        var to = 35
//        var i = from
//        var loopc = 0
//
//        while (i <= to) {
//            println(i)
//            i += n[loopc%n.size]
//            loopc++
//        }
        list.forEach {
            println(it)
        }
    }
}