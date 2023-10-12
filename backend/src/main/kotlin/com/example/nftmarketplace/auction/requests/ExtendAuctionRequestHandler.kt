package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.requests.commands.ExtendAuctionCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import org.springframework.stereotype.Component

interface ExtendAuctionRequestHandler {
    suspend fun handle(command: ExtendAuctionCommand)
}

@Component
class ExtendAuctionRequestHandlerImpl(
    val dbAuctionRepository: DbAuctionRepository
) : ExtendAuctionRequestHandler {
    override suspend fun handle(command: ExtendAuctionCommand) {
        dbAuctionRepository.get(command.auctionId)?.let { auction ->
            auction.extend(command.newTime)
        }
    }
}
