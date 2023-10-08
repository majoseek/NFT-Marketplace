package com.example.nftmarketplace.auction

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
) {
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
}
