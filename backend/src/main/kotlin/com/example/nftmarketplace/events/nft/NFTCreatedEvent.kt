package com.example.nftmarketplace.events.nft

import com.example.nftmarketplace.core.data.DomainEvent

data class NFTCreatedEvent(
    val contractAddress: String,
    val tokenId: Long,
) : DomainEvent(aggregateId = contractAddress to tokenId)
