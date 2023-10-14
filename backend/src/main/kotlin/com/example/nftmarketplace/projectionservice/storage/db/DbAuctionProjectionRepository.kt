package com.example.nftmarketplace.projectionservice.storage.db

import com.example.nftmarketplace.events.auctions.AuctionCancelledEvent
import com.example.nftmarketplace.events.auctions.AuctionCompletedEvent
import com.example.nftmarketplace.events.auctions.AuctionCreatedEvent
import com.example.nftmarketplace.events.auctions.AuctionExtendedEvent
import com.example.nftmarketplace.events.auctions.BidPlacedEvent
import com.example.nftmarketplace.events.nft.NFTCreatedEvent
import com.example.nftmarketplace.projectionservice.toAuctionProjectionEntity
import com.example.nftmarketplace.projectionservice.toBidProjection
import com.example.nftmarketplace.projectionservice.toNFTProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.datetime.toKotlinLocalDateTime
import org.springframework.stereotype.Component

interface DbAuctionProjectionRepository {

    suspend fun createAuction(auctionCreatedEvent: AuctionCreatedEvent)

    suspend fun extendAuction(auctionExtendedEvent: AuctionExtendedEvent)

    suspend fun placeBid(bidPlacedEvent: BidPlacedEvent)

    suspend fun completeAuction(auctionCompletedEvent: AuctionCompletedEvent)

    suspend fun cancelAuction(auctionCancelledEvent: AuctionCancelledEvent)

    suspend fun createNFT(nftCreatedEvent: NFTCreatedEvent)

    suspend fun getAuction(auctionId: Long): AuctionProjectionEntity?

    suspend fun getAuctions(
        startIndex: Long,
        endIndex: Long,
    ): Flow<AuctionProjectionEntity>

    suspend fun getTotalCount(): Long

    suspend fun getAuctionsByOwner(ownerAddress: String): Flow<AuctionProjectionEntity>

    suspend fun getAuctionsByStatus(status: String): Flow<AuctionProjectionEntity>

    suspend fun getAuctionsByContractAddress(contractAddress: String): Flow<AuctionProjectionEntity>

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionProjectionEntity?
}

@Component
class MongoProjectionAuctionRepository(
    private val repository: AuctionProjectionRepository,
) : DbAuctionProjectionRepository {

    override suspend fun createAuction(auctionCreatedEvent: AuctionCreatedEvent) {
        repository.save(auctionCreatedEvent.toAuctionProjectionEntity()).awaitSingleOrNull()
    }

    override suspend fun extendAuction(auctionExtendedEvent: AuctionExtendedEvent) {
        repository.findById(auctionExtendedEvent.auctionId).awaitSingleOrNull()?.let { auction ->
            repository.save(auction.copy(expiryTime = auctionExtendedEvent.newExpiryTime.toKotlinLocalDateTime())).awaitSingleOrNull()
        }

    }

    override suspend fun placeBid(bidPlacedEvent: BidPlacedEvent) {
        repository.findById(bidPlacedEvent.auctionId).awaitSingleOrNull()?.let { auction ->
            repository.save(auction.copy(bids = auction.bids + bidPlacedEvent.toBidProjection())).awaitSingleOrNull()
        }
    }

    override suspend fun completeAuction(auctionCompletedEvent: AuctionCompletedEvent) {
        repository.findById(auctionCompletedEvent.auctionId).awaitSingleOrNull()?.let { auction ->
            repository.save(
                auction.copy(
                    status = auctionCompletedEvent.winnerAddress?.let { AuctionProjectionEntity.Status.Won } ?: AuctionProjectionEntity.Status.Expired,
                    winner = auctionCompletedEvent.winnerAddress
                )
            ).awaitSingleOrNull()
        }
    }

    override suspend fun cancelAuction(auctionCancelledEvent: AuctionCancelledEvent) {
        repository.findById(auctionCancelledEvent.auctionId).awaitSingleOrNull()?.let { auction ->
            repository.save(auction.copy(status = AuctionProjectionEntity.Status.Cancelled)).awaitSingleOrNull()
        }
    }

    override suspend fun createNFT(nftCreatedEvent: NFTCreatedEvent) {
        repository.findByNftContractAddressAndNftTokenId(nftCreatedEvent.contractAddress, nftCreatedEvent.tokenId).awaitSingleOrNull()?.let { auction ->
            repository.save(auction.copy(nft = nftCreatedEvent.toNFTProjection())).awaitSingleOrNull()
        }
    }

    override suspend fun getAuction(auctionId: Long): AuctionProjectionEntity? {
        return repository.findById(auctionId).awaitSingleOrNull()
    }

    override suspend fun getAuctions(
        startIndex: Long,
        endIndex: Long,
    ): Flow<AuctionProjectionEntity> {
        val ids = (startIndex until endIndex).toList()
        return repository.findAllById(ids).asFlow()
    }

    override suspend fun getAuctionsByOwner(ownerAddress: String): Flow<AuctionProjectionEntity> {
        return repository.findAllByNftOwnerAddress(ownerAddress).asFlow()
    }

    override suspend fun getTotalCount(): Long {
        return repository.count().awaitSingleOrNull() ?: 0
    }

    override suspend fun getAuctionsByStatus(status: String): Flow<AuctionProjectionEntity> {
        runCatching {
            enumValueOf<AuctionProjectionEntity.Status>(status)
        }.getOrNull()?.let {
            return repository.findAllByStatus(it).asFlow()
        } ?: throw IllegalArgumentException("Invalid status $status")
    }

    override suspend fun getAuctionsByContractAddress(contractAddress: String): Flow<AuctionProjectionEntity> {
        return repository.findAllByNftContractAddress(contractAddress).asFlow()
    }

    override suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionProjectionEntity? {
        return repository.findByNftContractAddressAndNftTokenId(contractAddress, tokenId).awaitSingleOrNull()
    }
}


