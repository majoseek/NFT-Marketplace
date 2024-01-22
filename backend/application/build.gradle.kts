import org.springframework.boot.gradle.tasks.bundling.BootJar
plugins {
    id("io.gatling.gradle") version "3.10.3"
}

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
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.10.3")
    implementation("io.gatling:gatling-app:3.10.3")
    implementation("io.gatling:gatling-core:3.10.3")
}


tasks.withType<BootJar>() {
    mainClass.set("com.example.nftmarketplace.NftMarketplaceApplicationKt")
}


