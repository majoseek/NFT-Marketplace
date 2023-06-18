package com.example.nftmarketplace.auction

import com.example.nftmarketplace.nft.NFT
import com.example.nftmarketplace.toBidResponse
import java.math.BigDecimal

data class AuctionPagedResponse(
    val auctions: List<AuctionElement>,
    val page: Int,
    val size: Int,
    val count: Long
) 

fun NFTAuctionObject.toAuctionElement(): AuctionElement {
    return AuctionElement(
        auctionID = auctionID,
        title = title,
        description = description,
        nft = nft,
        startingPrice = startingPrice,
        reservePrice = reservePrice,
        minimumIncrement = minimumIncrement,
        expiryTime = expiryTime.toString(),
        highestBid = highestBid?.toBidResponse(),
        status = status,
    )
}

data class AuctionElement(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFTToken,
    val startingPrice: BigDecimal,
    val reservePrice: BigDecimal,
    val minimumIncrement: BigDecimal,
    val expiryTime: String,
    val highestBid: BidResponse? = null,
    val status: NFTAuctionObject.Status,
)

data class AuctionResponse(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFT,
    val startingPrice: BigDecimal,
    val reservePrice: BigDecimal,
    val minimumIncrement: BigDecimal,
    val expiryTime: String,
    val bids: List<BidResponse>? = null,
    val status: NFTAuctionObject.Status,
)

data class BidResponse(
    val bidder: String,
    val amount: BigDecimal,
    val timestamp: String,
)


