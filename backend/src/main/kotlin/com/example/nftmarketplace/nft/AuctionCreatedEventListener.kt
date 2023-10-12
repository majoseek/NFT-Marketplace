package com.example.nftmarketplace.nft

import com.example.nftmarketplace.configuration.RabbitQueueConfiguration
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.nft.requests.CreateNFTRequestHandler
import com.example.nftmarketplace.nft.requests.command.CreateNFTCommand
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired

@RabbitListener(queues = [RabbitQueueConfiguration.AUCTION_CREATED_QUEUE])
class AuctionCreatedEventListener(
    @Autowired private val createNFTRequestHandler: CreateNFTRequestHandler,
) {

    @RabbitHandler
    suspend fun receive(event: AuctionCreatedEvent) {
        createNFTRequestHandler.handle(
            command = CreateNFTCommand(
                contractAddress = event.nftContractAddress,
                tokenId = event.nftTokenId
            )
        )
    }
}
