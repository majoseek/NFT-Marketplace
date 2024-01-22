package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.requests.commands.PlaceBidCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import org.springframework.stereotype.Component

interface PlaceBidRequestHandler {
    suspend fun handle(command: PlaceBidCommand)
}

@Component
class PlaceBidRequestHandlerImpl(
    val dbAuctionRepository: DbAuctionRepository,
    val contractHelper: ContractHelper,
) : PlaceBidRequestHandler {
    override suspend fun handle(command: PlaceBidCommand) {
        dbAuctionRepository.get(command.auctionId)?.let { auction ->
            auction.placeBid(
                bidder = command.bidder,
                amount = command.amount,
                timestamp = command.timestamp
            )
            dbAuctionRepository.save(auction)
        } ?: contractHelper.getAuctionById(command.auctionId)?.let {
            dbAuctionRepository.save(it)
        } ?: throw AuctionNotFound(command.auctionId)
    }
}

class AuctionNotFound(val id: Long) : Exception("Auction with id $id not found")

