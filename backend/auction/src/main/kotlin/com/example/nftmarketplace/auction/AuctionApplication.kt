package com.example.nftmarketplace.auction

import com.example.nftmarketplace.events.RabbitInitializer
import com.example.nftmarketplace.events.RabbitQueueConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(RabbitQueueConfiguration::class, RabbitInitializer::class)
@SpringBootApplication
class AuctionApplication

fun main(args: Array<String>) {
    runApplication<AuctionApplication>(*args)
}