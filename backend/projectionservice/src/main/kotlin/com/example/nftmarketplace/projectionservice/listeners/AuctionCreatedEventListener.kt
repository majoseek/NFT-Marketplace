package com.example.nftmarketplace.projectionservice.listeners

import com.example.nftmarketplace.common.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.common.getLogger
import com.example.nftmarketplace.events.RabbitQueueConfiguration
import com.example.nftmarketplace.projectionservice.db.DbAuctionProjectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
// @DependsOn("RabbitInitializer")
class AuctionCreatedEventListenerProjection(
    @Autowired private val dbAuctionProjectionRepository: DbAuctionProjectionRepository,
) {
    init {
        getLogger().info("init + ${this::class.simpleName}")
    }

    @RabbitListener(queues = [RabbitQueueConfiguration.AUCTION_CREATED_PROJECTED_QUEUE])
    @RabbitHandler
    fun receive(event: AuctionCreatedEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            dbAuctionProjectionRepository.createAuction(event)
        }
    }
}

