import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.web3j.solidity.gradle.plugin.OutputComponent

plugins {
    id("java")
    id("org.web3j") version "4.10.3"
    id("org.web3j.solidity") version "0.4.0"
}

solidity {
    setOutputComponents(
        OutputComponent.BIN,
        OutputComponent.ABI,
    )
    executable = if (System.getProperties()["os.name"].toString().contains("windows")) {
        "./solc/solc-windows.exe"
    } else {
        "./solc/solc-static-linux"
    }
    version = "0.8.22"
    optimize = true
    optimizeRuns = 1000

}
node {
    nodeProjectDir.set(file(path = "./"))
}

web3j {
    generatedPackageName = "com.example.nftmarketplace.{0}"
    excludedContracts = listOf("Ownable")
    useNativeJavaTypes = true
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.web3j:core:4.10.3")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation(project(":common"))
    implementation(project(":restapi"))
    implementation(project(":events"))
}

tasks.withType<BootJar>() {
    mainClass.set("com.example.nftmarketplace.auction.AuctionApplicationKt")
}



val compileKotlin: KotlinCompile by tasks

compileKotlin.dependsOn("generateContractWrappers")
