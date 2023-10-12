package com.example.nftmarketplace.nft.data

import com.example.nftmarketplace.core.AggregateRoot
import com.example.nftmarketplace.events.nft.NFTCreatedEvent


data class NFT(
    val contractAddress: String,
    val tokenId: Long,
    val name: String,
    val description: String,
    val url: String,
    val type: Type,
    var ownerAddress: String? = null,
) : AggregateRoot() {
    enum class Type {
        Image,
        Video,
        Audio,
        Text,
        Other;
    }

    fun transfer(to: String) {
        ownerAddress = to
    }

    companion object {
        fun create(
            contractAddress: String,
            tokenId: Long,
            name: String,
            description: String,
            url: String,
            type: Type,
            ownerAddress: String? = null,
        ) = NFT(
            contractAddress = contractAddress,
            tokenId = tokenId,
            name = name,
            description = description,
            url = url,
            type = type,
            ownerAddress = ownerAddress
        ).apply {
            record(NFTCreatedEvent(contractAddress, tokenId))
        }
    }
}
