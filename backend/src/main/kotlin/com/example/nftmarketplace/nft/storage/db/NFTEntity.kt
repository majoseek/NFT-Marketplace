package com.example.nftmarketplace.nft.storage.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

@Document("nfts")
@CompoundIndex(name = "contract_id", def = "{'contractAddress': 1, 'tokenId': 1}", unique = true, sparse = true)
data class NFTEntity(
    @Id val id: NFTId,
    val name: String,
    val description: String,
    val url: String,
    val type: Type,
    val ownerAddress: String? = null,
    val auctionId: Long? = null,
) {
    enum class Type {
        Image,
        Video,
        Audio,
        Text,
        Other;
    }
}

data class NFTId(
    val contractAddress: String,
    val tokenId: Long,
) : Serializable
