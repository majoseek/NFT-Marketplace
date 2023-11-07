package com.example.nftmarketplace.auction.requests.commands

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class CreateAuctionCommand(
    val auctionId: Long,
    val title: String,
    val description: String,
    val nftContractAddress: String,
    val nftTokenId: Long,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: LocalDateTime,
    val bids: List<Bid>,
    val status: Status,
) {
    enum class Status {
        Active,
        Won,
        Expired,
        Canceled
    }

    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime
    )
}

