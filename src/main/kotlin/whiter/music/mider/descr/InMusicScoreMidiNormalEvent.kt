package whiter.music.mider.descr

import whiter.music.mider.*
import java.util.StringJoiner

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

class InMusicScoreEvent(val hex: ByteArray) : InMusicScore {

    constructor(hexData: String) : this(hexData.parseToMidiHexBytes())

    override val duration = DurationDescribe(default = .0) // unreachable

    override fun clone(): InMusicScore = InMusicScoreEvent(hex.clone())
}