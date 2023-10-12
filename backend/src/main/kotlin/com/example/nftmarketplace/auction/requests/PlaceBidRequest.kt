package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.requests.commands.PlaceBidCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import org.springframework.stereotype.Component

interface PlaceBidRequestHandler {
    suspend fun handle(placeBidCommand: PlaceBidCommand)
}

@Component
class PlaceBidRequestHandlerImpl(
    val dbAuctionRepository: DbAuctionRepository,
    val contractHelper: ContractHelper,
) : PlaceBidRequestHandler {
    override suspend fun handle(placeBidCommand: PlaceBidCommand) {
        dbAuctionRepository.get(placeBidCommand.auctionId)?.let { auction ->
            auction.placeBid(
                bidder = placeBidCommand.bidder,
                amount = placeBidCommand.amount,
                timestamp = placeBidCommand.timestamp
            )
            dbAuctionRepository.save(auction)
        } ?: contractHelper.getAuctionById(placeBidCommand.auctionId)?.let {
            dbAuctionRepository.save(it)
        } ?: throw AuctionNotFound(placeBidCommand.auctionId)
    }
}

class AuctionNotFound(val id: Long) : Exception("Auction with id $id not found")

