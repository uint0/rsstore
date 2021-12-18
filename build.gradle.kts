import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    application
}

group = "dev.uint0"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

application {
    mainClass.set("MainKt")
}
