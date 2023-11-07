package com.example.nftmarketplace.projectionservice.db

import kotlinx.datetime.LocalDateTime
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.math.BigDecimal


@Document(collection = "auctions-projection")
@CompoundIndex(name = "nft_id", def = "{'nft.contractAddress' : 1, 'nft.tokenId': 1}")
data class AuctionProjectionEntity(
    @Id val id: Long,
    val title: String,
    val description: String,
    val nft: NFT,
    val bids: List<Bid> = emptyList(),
    val expiryTime: LocalDateTime,
    val status: Status? = null,
    val startingPrice: BigDecimal? = null,
    val minimalIncrement: BigDecimal? = null,
    val winner: String? = null,
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

    // TODO()
    data class NFT(
        val contractAddress: String,
        val tokenId: Long,
        val name: String? = null,
        val description: String? = null,
        val url: String? = null,
        val type: Type? = null,
        val ownerAddress: String? = null,
    ) {
        enum class Type {
            Image,
            Video,
            Audio,
            Text,
            Other;
        }
    }
}
