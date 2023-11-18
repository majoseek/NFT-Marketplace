package com.example.nftmarketplace.analytic.storage.db

import kotlinx.datetime.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document
data class CollectionEntity(
    @Id
    val contractAddress: String,
    val collectionName: String,
    val collectionSymbol: String,
    val auctionsCreated: Long,
    val bidsPlaced: List<BidPlaced>
) {
    data class BidPlaced(
        val timestamp: LocalDateTime,
        val amount: BigDecimal,
    )
}
