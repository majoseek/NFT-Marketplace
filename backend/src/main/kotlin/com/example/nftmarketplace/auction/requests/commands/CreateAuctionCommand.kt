package com.example.nftmarketplace.auction.requests.commands

import com.example.nftmarketplace.auction.Auction
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
    val bids: List<Auction.Bid> = emptyList(),
    val highestBid: Auction.Bid? = null,
    val status: Auction.Status,
)

