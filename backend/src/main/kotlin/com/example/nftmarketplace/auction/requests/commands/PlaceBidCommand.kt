package com.example.nftmarketplace.auction.requests.commands

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class PlaceBidCommand(
    val auctionId: Long,
    val bidder: String,
    val amount: BigDecimal,
    val timestamp: LocalDateTime,
)

