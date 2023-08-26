package org.mider.impl

fun String.toByteArray(): ByteArray = encodeToByteArray()

fun ByteArray.clone(): ByteArray = byteArrayOf(*this)