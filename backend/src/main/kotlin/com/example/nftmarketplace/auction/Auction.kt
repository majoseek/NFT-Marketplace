package com.example.nftmarketplace.auction

import com.example.nftmarketplace.core.AggregateRoot
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class Auction(
    val auctionId: Long,
    val title: String,
    val description: String,
    val nft: NFT,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: LocalDateTime,
    val bids: List<Bid> = emptyList(),
    val highestBid: Bid? = null,
    val status: Status,
) : AggregateRoot() {
    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )

    enum class Status {
        Pending,
        Active,
        Cancelled,
        Expired,
        Won,
    }

    data class NFT(val contractAddress: String, val tokenId: Long)

    companion object {
        fun new(
            auctionId: Long,
            title: String,
            description: String,
            nft: NFT,
            startingPrice: BigDecimal?,
            reservePrice: BigDecimal?,
            minimumIncrement: BigDecimal?,
            expiryTime: LocalDateTime,
            bids: List<Bid> = emptyList(),
            highestBid: Bid? = null,
            status: Status = Status.Pending,
        ) = Auction(
            auctionId = auctionId,
            title = title,
            description = description,
            nft = nft,
            startingPrice = startingPrice,
            reservePrice = reservePrice,
            minimumIncrement = minimumIncrement,
            expiryTime = expiryTime,
            bids = bids,
            highestBid = highestBid,
            status = status,
        ).apply {
            record(
                AuctionCreatedEvent(
                    auctionId = auctionId,
                    nftContractAddress = nft.contractAddress,
                    nftTokenId = nft.tokenId
                )
            )
        }
    }
}
