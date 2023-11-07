package com.example.nftmarketplace.projectionservice.listeners
//
//import com.examle.nftmarketplace.events.RabbitQueueConfiguration
//import com.example.nftmarketplace.common.events.nft.NFTCreatedEvent
//import com.example.nftmarketplace.common.getLogger
//import com.example.nftmarketplace.projectionservice.storage.db.DbAuctionProjectionRepository
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.springframework.amqp.rabbit.annotation.RabbitHandler
//import org.springframework.amqp.rabbit.annotation.RabbitListener
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//
//@Component
//class NFTTransferredEventListener(
//    @Autowired private val dbAuctionProjectionRepository: DbAuctionProjectionRepository,
//) {
//    init {
//        getLogger().info(this::class.simpleName, "init")
//    }
//
//    @RabbitListener(queues = [RabbitQueueConfiguration.NFT_TRANSFERRED_ROUTING_KEY])
//    @RabbitHandler
//    fun receive(event: NFTT) {
//        CoroutineScope(Dispatchers.IO).launch {
//            dbAuctionProjectionRepository.tra(event)
//        }
//    }
//}
