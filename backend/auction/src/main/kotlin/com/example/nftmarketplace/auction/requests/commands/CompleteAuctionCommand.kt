package com.example.nftmarketplace.auction.requests.commands

data class CompleteAuctionCommand(
    val auctionId: Long,
    val winner: String?,
)
