/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("io.gitlab.arturbosch.detekt") version "1.13.1"
    id("org.jmailen.kotlinter") version "3.2.0"
    id("jacoco")

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // Publish build artifacts to an Apache Maven repository
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

allprojects {
    group = "ch.veehaitch.devicecheck"
    version = "0.1-SNAPSHOT"

    publishing {
        publications {
            create<MavenPublication>("default") {
                from(components["java"])
            }
        }

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/veehaitch/devicecheck-appattest")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
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

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0-M1")

    // CBOR
    val jacksonVersion = "2.11.3"
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Bouncy Castle
    implementation("org.bouncycastle:bcprov-jdk15on:1.66")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.66")

    // WebAuthN parsing
    implementation("com.webauthn4j:webauthn4j-core:0.14.0.RELEASE")

    // Logging
    implementation("org.slf4j:slf4j-nop:1.7.30")

    // Apache Commons Codecs: Base64 URL-safe
    implementation("commons-codec:commons-codec:1.15")

    // Kotest
    val kotestVersion = "4.3.0"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test

    // Testing of equals / hashcode
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.5")

    // MockWebServer
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")
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

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.isEnabled = true
    }
}

