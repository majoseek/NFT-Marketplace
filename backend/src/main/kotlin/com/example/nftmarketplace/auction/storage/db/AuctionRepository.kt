package com.example.nftmarketplace.auction.storage.db

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.toAuctionEntity
import com.example.nftmarketplace.core.EventPublisher
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
interface AuctionRepository : ReactiveMongoRepository<AuctionEntity, Long>


interface DbAuctionRepository {

    suspend fun create(auction: Auction): Long

    suspend fun get(id: Long): Auction?

    suspend fun save(auction: Auction)
}

@Component
class MongoAuctionRepository(
    @Autowired private val auctionRepository: AuctionRepository,
    @Autowired private val createAuctionEventPublisher: EventPublisher
): DbAuctionRepository {

    override suspend fun create(auction: Auction): Long {
        val auctionEntity = auction.toAuctionEntity()
        val savedAuction = auctionRepository.save(auctionEntity).awaitSingleOrNull()
        auction.events.forEach { createAuctionEventPublisher.publish(it) }
        return savedAuction?.id ?: throw IllegalStateException("Auction not saved")
    }

    override suspend fun get(id: Long) = auctionRepository.findById(id).awaitSingleOrNull()?.toAuction()

    override suspend fun save(auction: Auction) {
        auctionRepository.save(auction.toAuctionEntity()).awaitSingleOrNull()?.let {
            auction.events.forEach(createAuctionEventPublisher::publish)
        }

    }
}

fun AuctionEntity.toAuction() = Auction(
    auctionId = id,
    title = title,
    description = description,
    nft = Auction.NFT(nft.contractAddress, nft.tokenId),
    startingPrice = startingPrice,
    reservePrice = null,
    minimumIncrement = minimalIncrement,
    expiryTime = expiryTime,
    bids = mutableListOf(),
    status = Auction.Status.Pending,
)
