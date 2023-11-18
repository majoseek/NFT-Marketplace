rootProject.name = "NFTMarketplace"

pluginManagement {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}


include(
    ":auction",
    ":common",
    ":restapi",
    ":events",
    ":projectionservice",
    ":nft",
//    ":analytic",
    ":application"
)
