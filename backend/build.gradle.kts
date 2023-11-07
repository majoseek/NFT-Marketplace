import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.web3j.solidity.gradle.plugin.OutputComponent

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    id("java")
    id("org.web3j") version "4.10.0"
    id("org.web3j.solidity") version "0.3.6"
    kotlin("plugin.spring") version "1.7.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
solidity {
    setOutputComponents(
        OutputComponent.BIN,
        OutputComponent.ABI,
    )
    executable = "./solc/solc-windows.exe"
    version = "0.8.18"
    optimizeRuns = 200
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}
web3j {
    generatedPackageName = "com.example.nftmarketplace.{0}"
    excludedContracts = listOf("Ownable")
    useNativeJavaTypes = true
}


extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
//    implementation("org.springframework.boot:spring-boot-starter-mail")
//    implementation("org.springframework.boot:spring-boot-starter-quartz")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:+")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.web3j:core:4.10.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    languageVersion = "1.8"
}
