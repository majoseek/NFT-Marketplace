package com.example.nftmarketplace.auction

import com.example.nftmarketplace.restapi.auctions.BidElement
import kotlinx.coroutines.flow.Flow

interface AuctionQuery {
    suspend fun getAuctionsBids(auctionId: Long): Flow<List<BidElement>>
}
