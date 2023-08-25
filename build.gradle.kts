plugins {
    kotlin("multiplatform") version "1.9.0"
    id("org.jetbrains.dokka") version "1.8.20"
    `maven-publish`
    application
}

group = "org.mider"
version = "beta0.9.19"

repositories {
    mavenCentral()
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
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
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
                implementation ("cn.hutool:hutool-all:5.8.3")
                implementation("com.belerweb:pinyin4j:2.5.1")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
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