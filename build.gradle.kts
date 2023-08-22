buildscript {
    repositories {
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public") }
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.8.21" 
    id("org.jetbrains.dokka") version "1.8.20"
    `maven-publish`
    application
}


val projectVersion = "0.9.18"

group = "whiter.music"
version = projectVersion

repositories {
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation ("cn.hutool:hutool-all:5.8.3")
    testImplementation("com.belerweb:pinyin4j:2.5.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass.set("whiter.music.mider.practise.absolutepitch.RandomC2Kt")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.whiterasbk"
            artifactId = "mider"
            version = projectVersion
            from(components["kotlin"])
        }
    }
}
