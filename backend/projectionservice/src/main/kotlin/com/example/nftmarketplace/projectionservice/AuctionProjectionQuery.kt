package com.example.nftmarketplace.projectionservice

import com.example.nftmarketplace.projectionservice.db.DbAuctionProjectionRepository
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

interface AuctionProjectionQuery {

    suspend fun getAllAuctions(page: Int, count: Int, status: AuctionStatus? = null): Flow<AuctionsPagedResponse.AuctionElement>

    suspend fun getAuctionById(auctionId: Long): AuctionResponse?

    suspend fun getAuctionByOwner(ownerAddress: String): Flow<AuctionsPagedResponse.AuctionElement>

    suspend fun getAuctionByStatus(status: AuctionStatus): Flow<AuctionsPagedResponse.AuctionElement>

    suspend fun getTotalAuctions(): Long

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionResponse?

    suspend fun getAuctionsByContract(contractAddress: String): Flow<AuctionsPagedResponse.AuctionElement>
}

@Component
class AuctionProjectionAdapter(
    private val dbAuctionProjectionRepository: DbAuctionProjectionRepository
) : AuctionProjectionQuery {
    override suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: AuctionStatus?,
    ): Flow<AuctionsPagedResponse.AuctionElement> {
        val startIndex = (page - 1L) * count
        val endIndex = startIndex + count.toLong()
        return dbAuctionProjectionRepository.getAuctions(startIndex, endIndex).map {
            it.toAuctionElement()
        }
    }

    override suspend fun getAuctionById(auctionId: Long): AuctionResponse? {
        return dbAuctionProjectionRepository.getAuction(auctionId)?.toAuctionResponse()
    }

    override suspend fun getAuctionByOwner(ownerAddress: String): Flow<AuctionsPagedResponse.AuctionElement> {
        return dbAuctionProjectionRepository.getAuctionsByOwner(ownerAddress).map {
            it.toAuctionElement()
        }
    }

    override suspend fun getAuctionByStatus(status: AuctionStatus): Flow<AuctionsPagedResponse.AuctionElement> {
        return dbAuctionProjectionRepository.getAuctionsByStatus(status.toString()).map {
            it.toAuctionElement()
        }
    }

    override suspend fun getTotalAuctions(): Long {
        return dbAuctionProjectionRepository.getTotalCount()
    }

    override suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionResponse? {
        return dbAuctionProjectionRepository.getAuctionByNFT(contractAddress, tokenId)?.toAuctionResponse()
    }

    override suspend fun getAuctionsByContract(contractAddress: String): Flow<AuctionsPagedResponse.AuctionElement> {
        return dbAuctionProjectionRepository.getAuctionsByContractAddress(contractAddress).map {
            it.toAuctionElement()
        }
    }
}
