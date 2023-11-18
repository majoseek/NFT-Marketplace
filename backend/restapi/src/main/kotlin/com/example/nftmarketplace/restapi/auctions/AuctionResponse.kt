package com.example.nftmarketplace.restapi.auctions

import com.example.nftmarketplace.restapi.nfts.NFTResponse
import java.math.BigDecimal

data class AuctionResponse(
    val auctionId: Long,
    val title: String,
    val description: String,
    val nft: NFTResponse? = null,
    val startingPrice: BigDecimal? = null,
    val minimumIncrement: BigDecimal? = null,
    val expiryTime: String,
    val owner: String,
    val bids: List<BidElement>? = null,
    val status: AuctionStatus? = null,
    val winner: String? = null,
)
