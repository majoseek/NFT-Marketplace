dependencies {
    api("org.springframework.boot:spring-boot-starter-amqp")
    // jackon
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(project(":common"))
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    isEnabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
