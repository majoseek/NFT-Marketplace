package com.example.nftmarketplace.common.events.nft

import com.example.nftmarketplace.common.data.DomainEvent

data class NFTCreatedEvent(
    val contractAddress: String,
    val tokenId: Long,
    val name: String,
    val description: String,
    val url: String,
    val type: String,
) : DomainEvent(aggregateId = contractAddress to tokenId)
