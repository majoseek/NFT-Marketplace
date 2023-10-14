package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

data class AuctionCancelledEvent(
    val auctionId: Long
) : DomainEvent(aggregateId = auctionId)
