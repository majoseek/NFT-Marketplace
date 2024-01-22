package com.example.nftmarketplace.nft

import com.example.nftmarketplace.common.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.common.getLogger
import com.example.nftmarketplace.events.RabbitQueueConfiguration
import com.example.nftmarketplace.nft.requests.command.CreateNFTCommand
import kotlinx.coroutines.runBlocking
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AuctionCreatedEventListener(
    @Autowired private val auctionBuffer: com.example.nftmarketplace.nft.AuctionCreatedEventBuffer,
) {
    init {
        getLogger().info("${this::class.simpleName} init")
    }

    @RabbitListener(queues = [RabbitQueueConfiguration.AUCTION_CREATED_QUEUE])
    @RabbitHandler
    fun receive(event: AuctionCreatedEvent) {
        val command = CreateNFTCommand(event.nftContractAddress, event.nftTokenId)

        runBlocking {
            getLogger().info("Adding Command ${this::class.simpleName}")
            auctionBuffer.add(command)
        }
    }
}