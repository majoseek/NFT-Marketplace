package com.example.nftmarketplace.restapi.analytics.plots

import kotlinx.datetime.LocalDateTime

data class AuctionTrendData(
    val date: LocalDateTime,
    val totalAuctionCount: Long,
    val totalAuctionVolume: Long,
)
