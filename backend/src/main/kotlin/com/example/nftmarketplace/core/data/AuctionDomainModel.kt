package com.example.nftmarketplace.core.data

import com.example.nftmarketplace.auction.NFTToken
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal


data class AuctionDomainModel(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFTToken,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: LocalDateTime,
    val bids: List<Bid> = emptyList(),
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
        @JsonProperty(value = "won")
        Won, // 4
    }
    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )
}
