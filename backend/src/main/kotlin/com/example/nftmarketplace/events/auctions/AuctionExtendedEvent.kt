package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

data class AuctionExtendedEvent(
    val auctionId: Long,
    val newExpiryTime: Long,
) : DomainEvent(aggregateId = auctionId)