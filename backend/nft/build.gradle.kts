import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":events"))
    implementation(project(":restapi"))
    implementation(project(":common"))
    implementation("org.apache.tika:tika-core:2.9.0")
    implementation("org.apache.tika:tika-parsers-standard-package:2.9.0")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
}

tasks.withType<BootJar>() {
    mainClass.set("com.example.nftmarketplace.nft.NFTApplicationKt")
}
