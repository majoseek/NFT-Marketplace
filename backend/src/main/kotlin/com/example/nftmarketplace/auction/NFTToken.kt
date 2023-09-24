package com.example.nftmarketplace.auction

import com.example.nftmarketplace.nft.storage.db.NFTEntity

data class NFTToken(
    val address: String,
    val tokenID: Long,
    val url: String? = null,
    val type: NFTEntity.Type? = null,
    val name: String,
    val description: String,
    val ownerAddress: String?
)
