package whiter.music.mider.descr

import whiter.music.mider.*
import kotlin.contracts.Returns

/**
 * 位于乐谱中的 MIDI 事件
 */
class InMusicScoreMidiNormalEvent(val type: EventType, val args: ByteArray, val channel: Int): InMusicScore, Mute {

    override fun clone(): InMusicScoreMidiNormalEvent {
        return InMusicScoreMidiNormalEvent(type, args, channel)
    }

    override val duration = DurationDescribe(default = .0)

    override fun toString(): String = "[MIDIEvent:$type:${args.toList()}]"
}

class InMusicScoreMidiMetaEvent(val type: MetaEventType, val args: ByteArray): InMusicScore, Mute {

    override fun clone(): InMusicScoreMidiMetaEvent {
        return InMusicScoreMidiMetaEvent(type, args)
    }

    override val duration = DurationDescribe(default = .0)

    override fun toString(): String = "[MIDIMetaEvent:$type:${args.toList()}]"
}

class InMusicScoreEvent : InMusicScore {

    private var pure = true
    private lateinit var hexString: String
    private lateinit var hex: ByteArray
    private val octave: Int
    private val velocity: Int

    constructor(hex: ByteArray, octave: Int = 4, velocity: Int = 100) {
        pure = true
        this.hex = hex
        this.octave = octave
        this.velocity = velocity
    }

    constructor(hexData: String, octave: Int = 4, velocity: Int = 100) {
        pure = false
        hexString = hexData
        this.octave = octave
        this.velocity = velocity
    }

    override val duration = DurationDescribe(default = .0) // unreachable

    fun getHex(wholeTick: Int): ByteArray = if (pure) hex else hexString.trim().parseToMidiHex(wholeTick, octave, velocity)


    override fun clone(): InMusicScore = InMusicScoreEvent(hex.clone(), octave)
}