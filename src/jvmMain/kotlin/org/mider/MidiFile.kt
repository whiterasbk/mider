package org.mider

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

fun MidiFile.inStream(): InputStream {
    val bufferLength = getFileSize()
    val dst = ByteArray(bufferLength)
    doFinal().get(dst, 0, bufferLength)
    return ByteArrayInputStream(dst)
}

fun MidiFile.save(fileName: String) {

    val bufferLength = getFileSize()
    val dst = ByteArray(bufferLength)
    doFinal().get(dst, 0, bufferLength)

    val file = File(fileName)
    file.writeBytes(dst)

    file.setLastModified(System.currentTimeMillis())
}