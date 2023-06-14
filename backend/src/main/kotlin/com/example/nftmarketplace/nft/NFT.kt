package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.data.FileExtension

data class NFT(
    val contractAddress: String?,
    val tokenID: Long?,
    val name: String?,
    val ownerAddress: String?,
    val url: String?,
    val description: String,
    val type: Type,
) {
    enum class Type {
        Image,
        Video,
        Audio,
        Text,
        Other;
    }
}

