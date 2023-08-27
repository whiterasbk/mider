import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.util.Properties

plugins {
    kotlin("multiplatform") version "1.9.0"
    id("org.jetbrains.dokka") version "1.8.20"
    `maven-publish`
    application
}

group = "org.mider"
version = "beta0.9.19"

if (file("gpr.properties").exists()) {
    Properties().apply {
        load(file("gpr.properties").inputStream())
        rootProject.extraProperties["gpr.user"] = this["gpr.user"]
        rootProject.extraProperties["gpr.key"] = this["gpr.key"]
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven { url = uri("https://jitpack.io") }
    maven {
        url = uri("https://maven.pkg.github.com/whiterasbk/slowxml")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    js {
        useCommonJs()
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        nodejs()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.github.whiterasbk:slowxml:0.2.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:2.0.2")
                implementation("io.ktor:ktor-server-html-builder-jvm:2.0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("com.belerweb:pinyin4j:2.5.1")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("midi-player-js", "2.0.16"))
                implementation(npm("jzz-gui-player", "1.6.5"))
                implementation(npm("@tonejs/midi", "2.0.28"))
                implementation(npm("tone", "14.7.77"))
            }
        }

        val jsTest by getting
    }
}

application {
    mainClass.set("org.example.test.application.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

//tasks.named<KotlinJsCompile>("compileKotlinJs").configure {
//    kotlinOptions.moduleKind = "commonjs"
//}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.whiterasbk"
            artifactId = project.name
            version = project.version.toString()
            from(components["kotlin"])
        }
    }
}