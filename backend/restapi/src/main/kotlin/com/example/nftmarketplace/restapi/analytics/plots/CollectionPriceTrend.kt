package com.example.nftmarketplace.restapi.analytics.plots

import kotlinx.datetime.LocalDateTime

data class CollectionPriceTrend(
    val date: LocalDateTime,
    val averagePrice: Long,
)
