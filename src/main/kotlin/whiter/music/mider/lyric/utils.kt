package whiter.music.mider.lyric

import whiter.music.mider.code.getLyricAffectedNotes
import whiter.music.mider.descr.*

fun multiLyricResolve(lyric: String, list: List<InMusicScore>) {
    val words = lyric.split(" ")
    val affectNotes = getLyricAffectedNotes(list, words.size)
    affectNotes.forEachIndexed { lyricIndex, noc ->
        words[lyricIndex].let {
            if (it != "_") {
                when (noc) {
                    is Note -> noc.attach = NoteAttach(lyric = it)
                    is Chord -> noc.attach = ChordAttach(lyric = it)
                }
            }
        }
    }
}