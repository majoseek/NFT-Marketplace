package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.requests.commands.CancelAuctionCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import com.example.nftmarketplace.common.EventPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface CancelAuctionRequestHandler {
    suspend fun handle(command: CancelAuctionCommand)
}


@Component
class CancelAuctionRequestHandlerImpl(
    @Autowired private val dbAuctionRepository: DbAuctionRepository,
    private val eventPublisher: EventPublisher
) : CancelAuctionRequestHandler {
    override suspend fun handle(command: CancelAuctionCommand) {
        dbAuctionRepository.get(command.auctionId)?.let { auction ->
            auction.cancel()
            auction.getEvents().forEach(eventPublisher::publish)
        }
    }
}
