import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation(project(":common"))
    implementation(project(":events"))
    implementation(project(":restapi"))
}

tasks.withType<BootJar>() {
    mainClass.set("com.example.nftmarketplace.projectionservice.ProjectionServiceApplicationKt")
}