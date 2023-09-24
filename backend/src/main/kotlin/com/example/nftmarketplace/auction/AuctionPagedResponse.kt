package com.example.nftmarketplace.auction

import com.example.nftmarketplace.core.data.NFTDomainModel
import com.example.nftmarketplace.core.data.AuctionDomainModel
import com.example.nftmarketplace.toBidResponse
import java.math.BigDecimal

data class AuctionPagedResponse(
    val auctions: List<AuctionElement>,
    val page: Int,
    val size: Int,
    val count: Long
)

fun AuctionDomainModel.toAuctionElement(): AuctionElement {
    return AuctionElement(
        auctionID = auctionID,
        title = title,
        description = description,
        nft = NFTDomainModel(
            contractAddress = nft.address,
            tokenID = nft.tokenID,
            name = nft.name,
            url = nft.url,
            type = nft.type?.name?.let { NFTDomainModel.Type.valueOf(it) } ?: NFTDomainModel.Type.Other,
            description = nft.description,
            ownerAddress = nft.ownerAddress
        ),
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
    val nft: NFTDomainModel,
    val startingPrice: BigDecimal?,
    val reservePrice: BigDecimal?,
    val minimumIncrement: BigDecimal?,
    val expiryTime: String,
    val highestBid: BidResponse? = null,
    val status: AuctionDomainModel.Status,
)

data class AuctionResponse(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFTDomainModel,
    val startingPrice: BigDecimal? = null,
    val reservePrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: String,
    val bids: List<BidResponse> = emptyList(),
    val status: AuctionDomainModel.Status,
)

data class BidResponse(
    val bidder: String,
    val amount: BigDecimal,
    val timestamp: String,
)


