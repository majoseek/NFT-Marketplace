package com.example.nftmarketplace.restapi.nfts

data class NFTResponse(
    val contractAddress: String,
    val tokenId: Long,
    val name: String?,
    val url: String?,
    val ownerAddress: String?,
    val description: String?,
    val type: Type = Type.Other,
) {
    enum class Type {
        Image,
        Video,
        Audio,
        Text,
        Other;
    }
}
