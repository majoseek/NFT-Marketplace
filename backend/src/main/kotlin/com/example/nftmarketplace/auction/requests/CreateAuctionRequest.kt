package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.requests.commands.CreateAuctionCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import org.springframework.stereotype.Component

interface CreateAuctionRequestHandler {
    suspend fun handle(command: CreateAuctionCommand): Auction
}

@Component
class CreateAuctionRequestHandlerImpl(
    private val dbAuctionRepository: DbAuctionRepository,
) : CreateAuctionRequestHandler {
    override suspend fun handle(command: CreateAuctionCommand): Auction {
        val auction = Auction.create(
            auctionId = command.auctionId,
            title = command.title,
            description = command.description,
            nftContractAddress = command.nftContractAddress,
            nftTokenId = command.nftTokenId,
            startingPrice = command.startingPrice,
            reservePrice = command.reservePrice,
            minimumIncrement = command.minimumIncrement,
            expiryTime = command.expiryTime,
            bids = command.bids.map { Auction.Bid(it.bidder, it.amount, it.timestamp) },
            status = Auction.Status.valueOf(command.status.name),
        )


        dbAuctionRepository.create(auction)
        return auction
    }
}

