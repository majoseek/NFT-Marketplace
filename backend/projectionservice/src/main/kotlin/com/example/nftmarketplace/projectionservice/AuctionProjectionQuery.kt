package com.example.nftmarketplace.projectionservice

import com.example.nftmarketplace.projectionservice.db.AuctionProjectionEntity
import com.example.nftmarketplace.projectionservice.db.DbAuctionProjectionRepository
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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
        return status?.let {
            getAuctionByStatus(it)
        } ?: run {
            val startIndex = (page - 1L) * count
            val endIndex = startIndex + count.toLong()
            dbAuctionProjectionRepository.getAuctions(startIndex, endIndex).map {
                it.toAuctionElement()
            }
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

    override suspend fun getAuctionByStatus(
        status: AuctionStatus,
    ): Flow<AuctionsPagedResponse.AuctionElement> {
        return when (status) {
            AuctionStatus.Active,
            AuctionStatus.Expired,
            AuctionStatus.Canceled,
            AuctionStatus.Completed -> dbAuctionProjectionRepository.getAuctionsByStatus(status.toAuctionProjectionStatus())
            AuctionStatus.Ending -> dbAuctionProjectionRepository.getAuctionsEndingBefore(
                Clock.System.now().plus(endingAuctionDuration).toLocalDateTime(TimeZone.UTC)
            )
            AuctionStatus.AwaitingEnd -> dbAuctionProjectionRepository.getAuctionsByStatus(AuctionProjectionEntity.Status.Active).filter {
                it.expiryTime.toInstant(TimeZone.UTC) > Clock.System.now() && it.bids.isNotEmpty()
            }
        }.map { it.toAuctionElement() }
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
