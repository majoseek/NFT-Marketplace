package com.example.nftmarketplace.analytic.ranking

abstract class RankingItem<T> {
    abstract fun calculateScore(item: T): Double
}



