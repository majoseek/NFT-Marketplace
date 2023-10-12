package com.example.nftmarketplace.auction

import com.example.nftmarketplace.core.AggregateRoot
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.events.auctions.AuctionWonEvent
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class Auction(
    val auctionId: Long,
    val title: String,
    val description: String,
    val nft: NFT,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    var expiryTime: LocalDateTime,
    val bids: MutableList<Bid> = mutableListOf(),
//    val highestBid: Bid? = null,
    var status: Status,
) : AggregateRoot() {
    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
        val timestamp: LocalDateTime,
    )

    enum class Status {
        Pending,
        Active,
        Cancelled,
        Expired,
        Won,
    }

    data class NFT(
        val contractAddress: String,
        val tokenId: Long
    )

    fun placeBid(
        bidder: String,
        amount: BigDecimal,
        timestamp: LocalDateTime
    ) {
        bids.add(
            Bid(
                bidder = bidder,
                amount = amount,
                timestamp = timestamp
            )
        )
        record(events)
    }

    fun extend(newTime: LocalDateTime) {
        expiryTime = newTime
    }

    fun completeWithWinner(winner: String) {
        status = Status.Won
        record(
            AuctionWonEvent(
                auctionId = auctionId,
                winnerAddress = winner,
                contractAddress = nft.contractAddress,
                tokenId = nft.tokenId
            )
        )
    }

    fun completeAuctionWithoutWinner() {
        status = Status.Expired
    }

    companion object {
        fun create(
            auctionId: Long,
            title: String,
            description: String,
            nftContractAddress: String,
            nftTokenId: Long,
            startingPrice: BigDecimal?,
            reservePrice: BigDecimal?,
            minimumIncrement: BigDecimal?,
            expiryTime: LocalDateTime,
            bids: List<Bid> = emptyList(),
            status: Status = Status.Pending,
        ) = Auction(
            auctionId = auctionId,
            title = title,
            description = description,
            nft = NFT(nftContractAddress, nftTokenId),
            startingPrice = startingPrice,
            reservePrice = reservePrice,
            minimumIncrement = minimumIncrement,
            expiryTime = expiryTime,
            bids = bids.toMutableList(),
            status = status,
        ).apply {
            record(
                AuctionCreatedEvent(
                    auctionId = auctionId,
                    nftContractAddress = nft.contractAddress,
                    nftTokenId = nft.tokenId
                )
            )
        }
    }
}
