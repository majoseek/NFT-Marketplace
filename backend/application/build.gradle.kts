import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation(project(":events"))
    implementation(project(":restapi"))
    implementation(project(":common"))
    implementation(project(":auction"))
    implementation(project(":nft"))
    implementation(project(":projectionservice"))
}


tasks.withType<BootJar>() {
    mainClass.set("com.example.nftmarketplace.NftMarketplaceApplicationKt")
}


