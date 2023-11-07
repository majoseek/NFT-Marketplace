dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}


tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    isEnabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
