package com.example.nftmarketplace.auction

import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import com.example.nftmarketplace.restapi.auctions.BidElement
import kotlinx.coroutines.flow.Flow

interface AuctionQuery {
    suspend fun getAllAuctions(page: Int, count: Int, status: AuctionStatus? = null): Flow<AuctionsPagedResponse.AuctionElement>

    suspend fun getAuctionById(auctionId: Long): AuctionResponse

    suspend fun getAuctionByOwner(ownerAddress: String): List<AuctionResponse>

    suspend fun getAuctionByStatus(status: AuctionStatus): List<AuctionResponse>

    suspend fun getTotalAuctions(): Long

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionResponse?

    suspend fun getAuctionsByContract(contractAddress: String): List<AuctionResponse>

    suspend fun getAuctionsBids(auctionId: Long): Flow<BidElement>
}
