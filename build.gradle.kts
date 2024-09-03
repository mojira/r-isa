plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    application
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

val logBackVersion = "1.4.3"
val arrowVersion = "0.10.4"
val kotestVersion = "4.2.3"

dependencies {
    implementation(kotlin("stdlib-jdk8") as String) {
        isForce = true
        isChanging = true
    }
    implementation(kotlin("reflect") as String) {
        isForce = true
        isChanging = true
    }
    implementation("io.arrow-kt", "arrow-core", arrowVersion)
    implementation("io.arrow-kt", "arrow-syntax", arrowVersion)
    implementation("io.arrow-kt", "arrow-fx", arrowVersion)
    implementation("com.github.mattbdean", "JRAW", "v1.1.0")
    implementation("com.uchuhimo", "konf", "0.22.1")
    implementation("com.github.rcarz", "jira-client", "master-SNAPSHOT")
    implementation("com.github.napstr", "logback-discord-appender", "bda874138e")
    implementation("org.slf4j", "slf4j-api", "2.0.3")
    implementation("ch.qos.logback", "logback-classic", logBackVersion)
    implementation("ch.qos.logback", "logback-core", logBackVersion)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.13.+")

    testImplementation("io.kotest", "kotest-assertions-core-jvm", kotestVersion)
    testImplementation("io.kotest", "kotest-runner-junit5", kotestVersion)
    testImplementation("io.kotest", "kotest-assertions-arrow", kotestVersion)
    testImplementation("io.mockk", "mockk", "1.13.2")
    testImplementation("org.reflections", "reflections", "0.10.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    test {
        useJUnitPlatform()
        maxParallelForks =
            (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1 // Run with same number of cores
        reports.html.isEnabled = false
        reports.junitXml.isEnabled = false
    }
}

application {
    mainClassName = "io.github.mojira.risa.MainKt"
}

detekt {
    failFast = true // fail build on any finding
    buildUponDefaultConfig = true // preconfigure defaults
    parallel = true
    reports {
        html {
            destination = file("build/reports/detekt.html")
            enabled = true // observe findings in your browser with structure and code snippets
        }
        xml.enabled = false // checkstyle like format mainly for integrations like Jenkins
        txt.enabled = false // similar to the console output, contains issue signature to manually edit baseline files
    }
}

tasks {
    withType<io.gitlab.arturbosch.detekt.Detekt> {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        this.jvmTarget = "11"
    }
}
