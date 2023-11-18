package com.example.nftmarketplace.analytic.ranking

data class Ranking(
    val name: String,
    val description: String,
    val items: List<RankingItem<Double>>,
)
