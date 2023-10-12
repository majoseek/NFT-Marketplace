package com.example.nftmarketplace

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NftMarketplaceApplication

fun main(args: Array<String>) {
    runApplication<NftMarketplaceApplication>(*args)
}

fun getLogger() = LoggerFactory.getLogger("NFTMarketplaceApplication")
