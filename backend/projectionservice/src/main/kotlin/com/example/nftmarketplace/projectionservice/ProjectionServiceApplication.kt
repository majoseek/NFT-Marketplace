package com.example.nftmarketplace.projectionservice

import com.example.nftmarketplace.events.RabbitInitializer
import com.example.nftmarketplace.events.RabbitQueueConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(RabbitQueueConfiguration::class, RabbitInitializer::class)
@SpringBootApplication
class ProjectionServiceApplication

fun main(args: Array<String>) {
    runApplication<ProjectionServiceApplication>(*args)
}