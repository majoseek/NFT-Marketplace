package com.example.nftmarketplace.restapi.analytics

import org.springframework.stereotype.Controller

@Controller("/analytics")
interface AnalyticsApi {

    suspend fun getMostPopularCollections() {
        TODO()
    }

    suspend fun getMostPopularAuctions() {

    }

    suspend fun getCollectionPriceTrend() {

    }

    suspend fun getAuctionPriceTrend() {

    }

    suspend fun getAuctionVolumeTrend() {

    }
}
