package com.example.nftmarketplace.nft

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "nfts")
data class NFT(
    @org.springframework.data.annotation.Id
    val id: String? = null,
    val name: String,
    val ownerID: String,
    val type: Type,
    val description: String,
) {
    enum class Type {
        IMAGE,
        VIDEO,
        AUDIO,
        TEXT
    }
}

