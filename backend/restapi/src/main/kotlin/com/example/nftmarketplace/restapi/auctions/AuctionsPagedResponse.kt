package com.example.nftmarketplace.restapi.auctions

import com.example.nftmarketplace.restapi.nfts.NFTResponse

data class AuctionsPagedResponse(
    val auctions: List<AuctionElement>,
    val page: Int,
    val size: Int,
    val count: Long
) {
    data class AuctionElement(
        val auctionId: Long,
        val title: String,
        val description: String,
        val nft: NFTResponse?,
        val expiryTime: String,
        val owner: String,
        val highestBid: BidElement? = null,
        val status: AuctionStatus? = null,
    )
}

