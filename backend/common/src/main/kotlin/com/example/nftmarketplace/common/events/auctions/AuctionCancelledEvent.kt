package com.example.nftmarketplace.common.events.auctions

import com.example.nftmarketplace.common.data.DomainEvent

data class AuctionCancelledEvent(
    val auctionId: Long
) : DomainEvent(aggregateId = auctionId)
