package com.example.nftmarketplace.auction.utils

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.requests.CancelAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CompleteAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.CreateAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.ExtendAuctionRequestHandler
import com.example.nftmarketplace.auction.requests.PlaceBidRequestHandler
import com.example.nftmarketplace.auction.requests.commands.CancelAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.CompleteAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.CreateAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.ExtendAuctionCommand
import com.example.nftmarketplace.auction.requests.commands.PlaceBidCommand
import kotlinx.datetime.LocalDateTime

class TestCreateAuctionRequestHandler : CreateAuctionRequestHandler {
    var handledCommand: CreateAuctionCommand? = null

    override suspend fun handle(command: CreateAuctionCommand): Auction {
        handledCommand = command
        return Auction(
            auctionId = 0,
            title = "",
            description = "",
            nft = Auction.NFT(contractAddress = "", tokenId = 0),
            ownerAddress = "",
            startingPrice = null,
            reservePrice = null,
            minimumIncrement = null,
            expiryTime = LocalDateTime.parse("2021-01-01T00:00:00"),
            bids = mutableListOf(),
            status = Auction.Status.Active
        )
    }
}

class TestPlaceBidRequestHandler : PlaceBidRequestHandler {
    var handledCommand: PlaceBidCommand? = null

    override suspend fun handle(command: PlaceBidCommand) {
        handledCommand = command
    }
}

class TestExtendAuctionRequestHandler : ExtendAuctionRequestHandler {
    var handledCommand: ExtendAuctionCommand? = null

    override suspend fun handle(command: ExtendAuctionCommand) {
        handledCommand = command
    }
}

class TestCompleteAuctionRequestHandler : CompleteAuctionRequestHandler {
    var handledCommand: CompleteAuctionCommand? = null

    override suspend fun handle(command: CompleteAuctionCommand) {
        handledCommand = command
    }
}

class TestCancelAuctionRequestHandler : CancelAuctionRequestHandler {
    var handledCommand: CancelAuctionCommand? = null

    override suspend fun handle(command: CancelAuctionCommand) {
        handledCommand = command
    }
}
