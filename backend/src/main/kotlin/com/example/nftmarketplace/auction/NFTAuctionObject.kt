package com.example.nftmarketplace.auction

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal


data class NFTAuctionObject(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFTToken,
    val startingPrice: BigDecimal,
    val reservePrice: BigDecimal,
    val minimumIncrement: BigDecimal,
    val expiryTime: LocalDateTime,
    val bids: List<Bid>? = null,
    val highestBid: Bid? = null,
    val status: Status,
) {
    enum class Status {
        @JsonProperty(value = "pending")
        Pending, // 0
        @JsonProperty(value = "active")
        Active, // 1
        @JsonProperty(value = "canceled")
        Cancelled, // 2
        @JsonProperty(value = "expired")
        Expired, // 3
    }
    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )
}
