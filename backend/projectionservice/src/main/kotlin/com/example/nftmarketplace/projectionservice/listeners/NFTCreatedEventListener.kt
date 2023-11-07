package com.example.nftmarketplace.projectionservice.listeners

import com.example.nftmarketplace.common.events.nft.NFTCreatedEvent
import com.example.nftmarketplace.common.getLogger
import com.example.nftmarketplace.events.RabbitQueueConfiguration
import com.example.nftmarketplace.projectionservice.db.DbAuctionProjectionRepository
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
class NFTCreatedEventListener(
    @Autowired private val dbAuctionProjectionRepository: DbAuctionProjectionRepository,
) {
    init {
        getLogger().info(this::class.simpleName, "init")
    }

    @RabbitListener(queues = [RabbitQueueConfiguration.NFT_CREATED_QUEUE])
    @RabbitHandler
    fun receive(event: NFTCreatedEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            dbAuctionProjectionRepository.createNFT(event)
        }
    }
}

