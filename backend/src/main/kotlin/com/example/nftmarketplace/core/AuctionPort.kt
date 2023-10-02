package com.example.nftmarketplace.core

import com.example.nftmarketplace.core.data.AuctionDomainModel
import kotlinx.coroutines.flow.Flow

interface AuctionPort {
    suspend fun getAllAuctions(page: Int, count: Int, status: AuctionDomainModel.Status? = null): Flow<AuctionDomainModel>

    suspend fun getAuctionById(auctionId: Long): AuctionDomainModel

    suspend fun getAuctionByOwner(ownerAddress: String): List<AuctionDomainModel>

    suspend fun getAuctionByStatus(status: AuctionDomainModel.Status): List<AuctionDomainModel>

    suspend fun getTotalAuctions(): Long

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionDomainModel?

    suspend fun getAuctionsByContract(contractAddress: String): List<AuctionDomainModel>

    suspend fun getAuctionsBids(auctionId: Long): Flow<AuctionDomainModel.Bid>
}
