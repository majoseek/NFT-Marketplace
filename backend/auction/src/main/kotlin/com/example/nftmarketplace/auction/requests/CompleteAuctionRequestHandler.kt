package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.requests.commands.CompleteAuctionCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import org.springframework.stereotype.Component

interface CompleteAuctionRequestHandler {
    suspend fun handle(command: CompleteAuctionCommand)
}

@Component
class CompleteAuctionRequestHandlerImpl(
    val dbAuctionRepository: DbAuctionRepository
) : CompleteAuctionRequestHandler {
    override suspend fun handle(command: CompleteAuctionCommand) {
        dbAuctionRepository.get(command.auctionId)?.let { auction ->
            command.winner?.let {
                auction.completeWithWinner(it)
            } ?: run {
                auction.completeAuctionWithoutWinner()
            }
        }
    }
}
