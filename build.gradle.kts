plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "me.m64diamondstar"
version = "1.0-SNAPSHOT"
val ktorVersion = "3.2.3"

repositories {
    mavenCentral()

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // Test dependencies
    testImplementation(kotlin("test"))

    // Core dependencies
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("io.github.Xirado:JDA:b6a5a80b5c")
    implementation("club.minnced:jda-ktx:0.13.0")
    implementation("org.yaml:snakeyaml:2.5")

    // Algolia and its required dependencies
    implementation("com.algolia:algoliasearch-client-kotlin:3.26.0") {
        exclude(group = "io.ktor", module = "ktor-client-core") // Prevent version conflicts
    }

    // Ktor dependencies (must match version used by Algolia)
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Required runtime dependencies
    runtimeOnly("io.ktor:ktor-client-java:$ktorVersion") // Fallback client
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName.set("amber.jar")
    archiveClassifier.set("")

    manifest {
        attributes["Main-Class"] = "me.m64diamondstar.AmberKt"
    }

    mergeServiceFiles()
}

