package com.example.nftmarketplace.events.nft

import com.example.nftmarketplace.core.data.DomainEvent

data class NFTCreatedEvent(
    val contractAddress: String,
    val tokenId: Long,
    val name: String,
    val description: String,
    val url: String,
    val type: String,
    val ownerAddress: String? = null,
) : DomainEvent(aggregateId = contractAddress to tokenId)
