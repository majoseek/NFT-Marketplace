package com.example.nftmarketplace

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.example.nftmarketplace.common",
    "com.example.nftmarketplace.restapi",
    "com.example.nftmarketplace.auction",
    "com.example.nftmarketplace.nft",
    "com.example.nftmarketplace.projectionservice",
    "com.example.nftmarketplace.events",
])
class NftMarketplaceApplication

fun main(args: Array<String>) {
    runApplication<NftMarketplaceApplication>(*args)
}

fun getLogger() = LoggerFactory.getLogger("NFTMarketplaceApplication")
