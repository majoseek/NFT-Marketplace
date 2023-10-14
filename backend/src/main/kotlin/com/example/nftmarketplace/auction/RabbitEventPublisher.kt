package com.example.nftmarketplace.auction

import com.example.nftmarketplace.configuration.RabbitQueueConfiguration
import com.example.nftmarketplace.core.EventPublisher
import com.example.nftmarketplace.core.data.DomainEvent
import com.example.nftmarketplace.events.auctions.AuctionCompletedEvent
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.events.auctions.AuctionExtendedEvent
import com.example.nftmarketplace.events.auctions.BidPlacedEvent
import com.example.nftmarketplace.events.nft.NFTCreatedEvent
import com.example.nftmarketplace.events.nft.NFTTransferredEvent
import com.example.nftmarketplace.getLogger
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitEventPublisher(
    private val rabbitTemplate: RabbitTemplate,
) : EventPublisher {

    init {
        getLogger().info("init ${this::class.simpleName}")
    }

    override fun publish(event: DomainEvent) {
        val properties = event.getQueueProperties()
        rabbitTemplate.convertAndSend(
            properties.exchange,
            properties.routingKey,
            event
        )
    }
}

private class QueueProperties(
    val exchange: String,
    val routingKey: String,
)

private fun DomainEvent.getQueueProperties() = when (this) {
    is AuctionCreatedEvent -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.AUCTION_CREATED_ROUTING_KEY,
    )
    is BidPlacedEvent -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.AUCTION_BID_PLACED_ROUTING_KEY,
    )
    is AuctionExtendedEvent -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.AUCTION_EXTENDED_ROUTING_KEY,
    )
    is AuctionCompletedEvent -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.AUCTION_COMPLETED_ROUTING_KEY,
    )
    is NFTCreatedEvent -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.NFT_CREATED_ROUTING_KEY,
    )
    is NFTTransferredEvent -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.NFT_TRANSFERRED_ROUTING_KEY,
    )
    else -> throw IllegalArgumentException("No queue properties found for event $this")
}
