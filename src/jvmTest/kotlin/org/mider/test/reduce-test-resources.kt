package org.mider.test

import org.mider.dsl.MiderDSL
import org.mider.dsl.apply
import java.io.File

val testResourcesDirPath: String get() = "src/jvmTest/resources/"
val testResourcesDir: File get() = File(testResourcesDirPath)

fun applyInTestResDir(filename: String, block: MiderDSL.() -> Unit) = apply(testResourcesDirPath + filename, block)

