package com.example.nftmarketplace.restapi.auctions

import com.example.nftmarketplace.restapi.nfts.NFTResponse
import java.math.BigDecimal

data class AuctionResponse(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFTResponse,
    val startingPrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: String,
    val bids: List<BidElement> = emptyList(),
    val status: AuctionStatus
)
