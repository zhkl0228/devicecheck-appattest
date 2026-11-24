import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "2.4.21"
    id("dev.detekt") version "2.0.0-alpha.6"
    id("org.jmailen.kotlinter") version "5.7.0"
    id("jacoco")
    id("io.github.ben-manes.versions") version "0.67.0"
    id("org.jetbrains.dokka") version "2.3.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // Publish build artifacts to an Apache Maven repository
    `maven-publish`

    // Sign artifacts
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)

        // For creation of default methods in interfaces
        jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
    }
}

allprojects {
    group = "ch.veehait.devicecheck"
    val baseVersion = "0.9.6"

    // Add the "-SNAPSHOT" suffix if the CI wasn't triggered by a new release
    version = when {
        System.getenv("GITHUB_EVENT_NAME") != "release" -> "$baseVersion-SNAPSHOT"
        else -> baseVersion
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set("Server-side library to validate the authenticity of Apple App Attest artifacts," +
                            " written in Kotlin")
                    url.set("https://github.com/veehaitch/devicecheck-appattest")
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("veehaitch")
                            name.set("Vincent Haupert")
                            email.set("mail@vincent-haupert.de")
                        }
                    }
                    scm {
                        connection.set("scm:git:git@github.com:veehaitch/devicecheck-appattest.git")
                        developerConnection.set("scm:git:git@github.com:veehaitch/devicecheck-appattest.git")
                        url.set("https://github.com/veehaitch/devicecheck-appattest")
                    }
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            // env: ORG_GRADLE_PROJECT_sonatypeUsername
            username.set(project.findProperty("sonatypeUsername") as String?)
            // env: ORG_GRADLE_PROJECT_sonatypePassword
            password.set(project.findProperty("sonatypePassword") as String?)
        }
    }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // CBOR
    val jacksonVersion = "2.13.4"
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Bouncy Castle
    val bouncyCastleVersion = "1.73"
    implementation("org.bouncycastle:bcpkix-jdk18on:$bouncyCastleVersion")

    // Kotest
    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion") // for kotest property test

    // Testing of equals / hashcode
    testImplementation("nl.jqno.equalsverifier:equalsverifier:3.19.4")

    // MockWebServer
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")

    // JWS issuing
    testImplementation("com.nimbusds:nimbus-jose-jwt:9.25")

    // Google Guava: Bytes.indexOf
    testImplementation("com.google.guava:guava:31.1-jre")
}

dependencyLocking {
    lockMode.set(LockMode.STRICT)
    lockAllConfigurations()
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
}

// detekt's own Kotlin compiler takes the running JDK for the target, which it may not know
tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    jvmTarget = "11"
}
tasks.withType<dev.detekt.gradle.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

jacoco {
    toolVersion = "0.8.16"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
    }
}

dokka {
    moduleName.set("Apple App Attest Kotlin Library")

    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    dokkaSourceSets.main {
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl("https://github.com/veehaitch/devicecheck-appattest/tree/main/src/main/kotlin")
            remoteLineSuffix.set("#L")
        }
    }
}
