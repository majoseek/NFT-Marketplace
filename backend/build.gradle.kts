import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

val solidityVersion by rootProject.extra { "0.8.20" }

plugins {
    id("java")
    id ("de.undercouch.download") version "5.5.0" apply false
    id("org.springframework.boot") version "3.0.4" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    kotlin("jvm") version "1.8.20" apply false
    kotlin("plugin.spring") version "1.8.20" apply false
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }

    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    val implementation by configurations

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
            jvmTarget = "19"
        }
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(19))
        }
    }
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
        }
    }
}

tasks.withType<BootJar>() {
    mainClass.set("com.example.nftmarketplace.NftMarketplaceApplicationKt")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

tasks.register<Download>("downloadSolc") {
    val osName = System.getProperty("os.name").toLowerCase()
    val solcBinaryName = if (osName.contains("windows")) "solc-windows.exe" else "solc-static-linux"
    val solcUrl = "https://github.com/ethereum/solidity/releases/download/v$solidityVersion/$solcBinaryName"

    src(solcUrl)
    println("Downloading solc from $solcUrl")
    dest("./solc/$solcBinaryName")
    overwrite(false)
    project.exec {
        workingDir("./solc")
        commandLine("chmod", "+x", solcBinaryName)
    }
}
