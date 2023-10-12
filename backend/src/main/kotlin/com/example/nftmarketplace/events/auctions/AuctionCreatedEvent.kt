package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

data class AuctionCreatedEvent(
    val auctionId: Long,
    val nftContractAddress: String,
    val nftTokenId: Long,
) : DomainEvent(aggregateId = auctionId)
