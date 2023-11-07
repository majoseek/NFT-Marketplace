package com.example.nftmarketplace.projectionservice

import com.example.nftmarketplace.common.enumValueOrNull
import com.example.nftmarketplace.common.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.common.events.auctions.BidPlacedEvent
import com.example.nftmarketplace.common.events.nft.NFTCreatedEvent
import com.example.nftmarketplace.projectionservice.db.AuctionProjectionEntity
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import com.example.nftmarketplace.restapi.auctions.BidElement
import com.example.nftmarketplace.restapi.nfts.NFTResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import java.math.BigDecimal
import kotlin.time.Duration.Companion.days

val endingAuctionDuration = 1.days


fun AuctionCreatedEvent.toAuctionProjectionEntity() = AuctionProjectionEntity(
    id = auctionId,
    title = title,
    description = description,
    nft = AuctionProjectionEntity.NFT(
        contractAddress = nftContractAddress,
        tokenId = nftTokenId,
    ),
    bids = bids.map { AuctionProjectionEntity.Bid(it.bidder, BigDecimal(it.amount), LocalDateTime.parse(it.timestamp)) },
    expiryTime = LocalDateTime.parse(expiryTime),
    status = when (status) {
        AuctionCreatedEvent.Status.Active -> AuctionProjectionEntity.Status.Active
        AuctionCreatedEvent.Status.Won -> AuctionProjectionEntity.Status.Won
        AuctionCreatedEvent.Status.Expired -> AuctionProjectionEntity.Status.Expired
        AuctionCreatedEvent.Status.Canceled -> AuctionProjectionEntity.Status.Canceled
    },
    startingPrice = BigDecimal(startingPrice),
    minimalIncrement = BigDecimal(minimalIncrement),
)

fun BidPlacedEvent.toBidProjection() = AuctionProjectionEntity.Bid(
    bidder = bidderAddress, amount = amount, timestamp = timestamp.toKotlinLocalDateTime()
)

fun NFTCreatedEvent.toNFTProjection() = AuctionProjectionEntity.NFT(
    contractAddress = contractAddress,
    tokenId = tokenId,
    name = name,
    description = description,
    url = url,
    type = enumValueOrNull<AuctionProjectionEntity.NFT.Type>(type),
    ownerAddress = ownerAddress,
)

fun AuctionProjectionEntity.toAuctionElement() = AuctionsPagedResponse.AuctionElement(
    auctionID = id,
    title = title,
    description = description,
    nft = NFTResponse(
        contractAddress = nft.contractAddress,
        tokenId = nft.tokenId,
        name = nft.name,
        description = nft.description,
        url = nft.url,
        type = nft.type?.let { enumValueOrNull<NFTResponse.Type>(it.name) } ?: NFTResponse.Type.Other,
        ownerAddress = nft.ownerAddress,
    ),
    expiryTime = expiryTime.toString(),
    status = status?.toAuctionResponseStatus(expiryTime),
    highestBid = bids.getHighestBidElement()
)

fun List<AuctionProjectionEntity.Bid>.getHighestBidElement() = maxByOrNull { it.amount }?.let {
    BidElement(
        bidder = it.bidder,
        amount = it.amount,
        timestamp = it.timestamp.toString()
    )
}

fun AuctionProjectionEntity.Status.toAuctionResponseStatus(expiryTime: LocalDateTime) = when (this) {
    AuctionProjectionEntity.Status.Active -> {
        if (expiryTime.toInstant(TimeZone.UTC).minus(endingAuctionDuration) < Clock.System.now()) {
            AuctionStatus.Ending
        } else {
            AuctionStatus.Active
        }
    }
    AuctionProjectionEntity.Status.Canceled -> AuctionStatus.Canceled
    AuctionProjectionEntity.Status.Expired -> AuctionStatus.Expired
    AuctionProjectionEntity.Status.Won -> AuctionStatus.Completed
}

fun AuctionStatus.toAuctionProjectionStatus() = when (this) {
    AuctionStatus.Active -> AuctionProjectionEntity.Status.Active
    AuctionStatus.Expired -> AuctionProjectionEntity.Status.Expired
    AuctionStatus.Canceled -> AuctionProjectionEntity.Status.Canceled
    AuctionStatus.Ending -> throw RuntimeException("Auction status Ending is not supported")
    AuctionStatus.Completed -> AuctionProjectionEntity.Status.Won
}

fun AuctionProjectionEntity.toAuctionResponse() = AuctionResponse(
    auctionId = id,
    title = title,
    description = description,
    nft = NFTResponse(
        contractAddress = nft.contractAddress,
        tokenId = nft.tokenId,
        name = nft.name,
        description = nft.description,
        url = nft.url,
        type = nft.type?.let { enumValueOrNull<NFTResponse.Type>(it.name) } ?: NFTResponse.Type.Other,
        ownerAddress = nft.ownerAddress,
    ),
    bids = bids.map {
        BidElement(
            bidder = it.bidder,
            amount = it.amount,
            timestamp = it.timestamp.toString()
        )
    },
    expiryTime = expiryTime.toString(),
    status = status?.toAuctionResponseStatus(expiryTime),
    startingPrice = startingPrice,
    minimumIncrement = minimalIncrement,
    winner = winner,
)
