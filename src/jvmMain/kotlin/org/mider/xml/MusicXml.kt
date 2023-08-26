package org.mider.xml

import java.io.File

fun MusicXml.save(path: String) = save(File(path))

fun MusicXml.save(file: File) = file.writeText(toString())
