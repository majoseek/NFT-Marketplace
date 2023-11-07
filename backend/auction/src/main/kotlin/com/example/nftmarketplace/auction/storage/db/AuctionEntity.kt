package com.example.nftmarketplace.auction.storage.db

import kotlinx.datetime.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.math.BigDecimal

@Document(collection = "auctions")
data class AuctionEntity(
    @Id val id: Long,
    val title: String,
    val description: String,
    val nft: NFTId,
    val bids: List<Bid> = emptyList(),
    val expiryTime: LocalDateTime,
    val status: Status,
    val startingPrice: BigDecimal? = null,
    val minimalIncrement: BigDecimal? = null,
) : Serializable {
    enum class Status {
        Active,
        Won,
        Expired,
        Canceled
    }

    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )

    data class NFTId(
        val contractAddress: String,
        val tokenId: Long,
    ) : Serializable
}
