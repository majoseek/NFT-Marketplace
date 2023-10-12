package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

interface CreateAuctionRequestHandler {
    suspend fun handle(request: CreateAuctionCommand): Auction
}

class CreateAuctionRequestHandlerImpl(
    val dbAuctionRepository: DbAuctionRepository
) : CreateAuctionRequestHandler {
    override suspend fun handle(request: CreateAuctionCommand): Auction {
        val auction = Auction.new(
            auctionId = request.auctionId,
            title = request.title,
            description = request.description,
            nft = request.nft,
            startingPrice = request.startingPrice,
            reservePrice = request.reservePrice,
            minimumIncrement = request.minimumIncrement,
            expiryTime = request.expiryTime,
            bids = request.bids,
            highestBid = request.highestBid,
            status = request.status,
        )
        dbAuctionRepository.create(auction)
        return auction
    }
}

data class CreateAuctionCommand(
    val auctionId: Long,
    val title: String,
    val description: String,
    val nft: Auction.NFT,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: LocalDateTime,
    val bids: List<Auction.Bid> = emptyList(),
    val highestBid: Auction.Bid? = null,
    val status: Auction.Status,
)
