package com.example.nftmarketplace.nft.data


data class NFT(
    val contractAddress: String,
    val tokenId: Long,
    val name: String,
    val description: String,
    val url: String,
    val type: Type,
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
