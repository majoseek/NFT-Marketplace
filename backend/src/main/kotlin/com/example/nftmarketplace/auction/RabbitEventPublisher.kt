package com.example.nftmarketplace.auction

import com.example.nftmarketplace.configuration.RabbitQueueConfiguration
import com.example.nftmarketplace.core.EventPublisher
import com.example.nftmarketplace.core.data.DomainEvent
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.events.auctions.AuctionExtendedEvent
import com.example.nftmarketplace.events.auctions.BidPlacedEvent
import com.example.nftmarketplace.events.auctions.NFTTransfered
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RabbitEventPublisher(
    @Autowired private val rabbitTemplate: RabbitTemplate,
) : EventPublisher {

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
    is NFTTransfered -> QueueProperties(
        RabbitQueueConfiguration.EXCHANGE,
        RabbitQueueConfiguration.NFT_TRANSFERRED_ROUTING_KEY,
    )
    else -> throw IllegalArgumentException("No queue properties found for event $this")
}
