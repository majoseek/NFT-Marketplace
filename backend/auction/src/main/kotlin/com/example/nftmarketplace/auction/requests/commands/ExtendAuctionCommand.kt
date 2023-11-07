package com.example.nftmarketplace.auction.requests.commands

import kotlinx.datetime.LocalDateTime

data class ExtendAuctionCommand(
    val auctionId: Long,
    val newTime: LocalDateTime
)
