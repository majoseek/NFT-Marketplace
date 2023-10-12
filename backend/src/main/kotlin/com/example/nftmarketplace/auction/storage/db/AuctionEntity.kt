package com.example.nftmarketplace.auction.storage.db

import com.example.nftmarketplace.nft.storage.db.NFTId
import kotlinx.datetime.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger

@Document(collection = "auctions")
data class AuctionEntity(
    @Id val id: Long,
    val title: String,
    val description: String,
    val nft: NFTId,
    val currentBid: CurrentBid? = null,
    val expiryTime: LocalDateTime,
    val status: Status,
    val startingPrice: BigDecimal? = null,
    val minimalIncrement: BigDecimal? = null,
) : Serializable {
    enum class Status {
        NotStared, Active, Cancelled, Expired, Won
    }

    data class CurrentBid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )
}