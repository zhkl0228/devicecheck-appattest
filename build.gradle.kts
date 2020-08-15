/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("io.gitlab.arturbosch.detekt") version "1.10.0"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // CBOR
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.11.+")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.2") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:4.1.2") // for kotest property test
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
}

tasks.withType<Test> {
  useJUnitPlatform()
}
