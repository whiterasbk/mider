package whiter.music.mider.descr

import whiter.music.mider.EventType
import whiter.music.mider.MetaEventType

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