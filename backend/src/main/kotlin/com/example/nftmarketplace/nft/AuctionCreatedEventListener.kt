package com.example.nftmarketplace.nft

import com.example.nftmarketplace.configuration.RabbitQueueConfiguration
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.getLogger
import com.example.nftmarketplace.nft.requests.CreateNFTRequestHandler
import com.example.nftmarketplace.nft.requests.command.CreateNFTCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AuctionCreatedEventListener(
    @Autowired private val createNFTRequestHandler: CreateNFTRequestHandler,
) {

    init {
        getLogger().info("Setting up AuctionCreated Listener")
    }

    @RabbitListener(
        queues = [RabbitQueueConfiguration.AUCTION_CREATED_QUEUE],
    )
    @RabbitHandler
    fun receive(event: AuctionCreatedEvent) {
//        println("Received null event: $event")
        CoroutineScope(Dispatchers.Default).launch {
            createNFTRequestHandler.handle(
                command = CreateNFTCommand(
                    contractAddress = event.nftContractAddress,
                    tokenId = event.nftTokenId
                )
            )
        }

    }
}
