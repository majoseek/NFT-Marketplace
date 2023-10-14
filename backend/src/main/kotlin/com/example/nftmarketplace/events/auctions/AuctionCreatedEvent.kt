package com.example.nftmarketplace.events.auctions

import com.example.nftmarketplace.core.data.DomainEvent

data class AuctionCreatedEvent(
    val auctionId: Long,
    val nftContractAddress: String,
    val nftTokenId: Long,
    val title: String,
    val description: String,
    val expiryTime: String,
    val startingPrice: String,
    val minimalIncrement: String,
    val status: String,
    val bids: List<Bid> = emptyList(),
) : DomainEvent(aggregateId = auctionId) {
    data class Bid(
        val bidder: String,
        val amount: String,
        val timestamp: String,
    )
}
