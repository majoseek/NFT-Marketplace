import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.web3j.solidity.gradle.plugin.OutputComponent


plugins {
    id("java")
    id("org.web3j") version "4.10.3"
    id("org.web3j.solidity") version "0.4.0"
}

solidity {
    setOutputComponents(
        OutputComponent.BIN,
        OutputComponent.ABI
    )
    val osName = System.getProperty("os.name").toLowerCase()
    val solcBinaryName = if (osName.contains("windows")) "solc-windows.exe" else "solc-static-linux"
    val executablePath = rootProject.layout.projectDirectory.dir("solc").file(solcBinaryName).asFile
    executable = executablePath.absolutePath
    version = rootProject.extra.get("solidityVersion") as String
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
    implementation(project(":common"))
    implementation(project(":restapi"))
    implementation(project(":events"))
}
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    isEnabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.dependsOn("generateContractWrappers")

