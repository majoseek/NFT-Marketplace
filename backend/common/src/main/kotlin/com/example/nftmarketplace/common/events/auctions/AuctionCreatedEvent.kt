package com.example.nftmarketplace.common.events.auctions

import com.example.nftmarketplace.common.data.DomainEvent

data class AuctionCreatedEvent(
    val auctionId: Long,
    val ownerAddress: String,
    val nftContractAddress: String,
    val nftTokenId: Long,
    val title: String,
    val description: String,
    val expiryTime: String,
    val startingPrice: String,
    val minimalIncrement: String,
    val status: Status,
    val bids: List<Bid> = emptyList(),
) : DomainEvent(aggregateId = auctionId) {
    data class Bid(
        val bidder: String,
        val amount: String,
        val timestamp: String,
    )

    enum class Status {
        Active,
        Won,
        Expired,
        Canceled
    }
}
