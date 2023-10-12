package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

data class BidPlacedEvent(
    val auctionId: Long,
    val bidderAddress: String,
    val amount: Long,
    val timestamp: Long,
) : DomainEvent(aggregateId = auctionId)
