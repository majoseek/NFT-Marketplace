package com.example.nftmarketplace.auction.requests

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.requests.commands.CreateAuctionCommand
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository

interface CreateAuctionRequestHandler {
    suspend fun handle(command: CreateAuctionCommand): Auction
}

class CreateAuctionRequestHandlerImpl(
    val dbAuctionRepository: DbAuctionRepository
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
            bids = command.bids,
            status = command.status,
        )
        dbAuctionRepository.create(auction)
        return auction
    }
}

