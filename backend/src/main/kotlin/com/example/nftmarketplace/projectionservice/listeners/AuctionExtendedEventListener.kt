package com.example.nftmarketplace.projectionservice.listeners

import com.example.nftmarketplace.configuration.RabbitQueueConfiguration
import com.example.nftmarketplace.events.auctions.AuctionExtendedEvent
import com.example.nftmarketplace.getLogger
import com.example.nftmarketplace.projectionservice.storage.db.DbAuctionProjectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

@Component
@DependsOn("rabbitInitializer")
class AuctionExtendedEventListener(
    @Autowired private val dbAuctionProjectionRepository: DbAuctionProjectionRepository,
) {
    init {
        getLogger().info(this::class.simpleName, "init")
    }

    @RabbitListener(queues = [RabbitQueueConfiguration.AUCTION_EXTENDED_QUEUE])
    @RabbitHandler
    fun receive(event: AuctionExtendedEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            dbAuctionProjectionRepository.extendAuction(event)
        }
    }
}
