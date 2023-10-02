package com.example.nftmarketplace.restapi.nfts

data class NFTResponse(
    val contractAddress: String,
    val tokenID: Long,
    val name: String?,
    val ownerAddress: String?,
    val url: String?,
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
