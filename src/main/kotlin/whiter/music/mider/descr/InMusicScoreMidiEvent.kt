package whiter.music.mider.descr

import whiter.music.mider.EventType

/**
 * 位于乐谱中的 MIDI 事件
 */
class InMusicScoreMidiEvent(val type: EventType, val args: ByteArray, val channel: Int): InMusicScore, Mute {

    override fun clone(): InMusicScoreMidiEvent {
        return InMusicScoreMidiEvent(type, args, channel)
    }

    override val duration = DurationDescribe(default = .0)
}