package com.example.nftmarketplace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class NftMarketplaceApplication

fun main(args: Array<String>) {
    runApplication<NftMarketplaceApplication>(*args)
}
