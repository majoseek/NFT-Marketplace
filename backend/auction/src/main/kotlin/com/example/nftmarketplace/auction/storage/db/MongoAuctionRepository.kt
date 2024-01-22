package com.example.nftmarketplace.auction.storage.db

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.toAuctionEntity
import com.example.nftmarketplace.common.EventPublisher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


interface DbAuctionRepository {

    suspend fun create(auction: Auction): Long

    suspend fun get(id: Long): Auction?

    suspend fun save(auction: Auction)

    suspend fun getAll(list: List<Long>): Flow<Auction>
}

@Component
class MongoAuctionRepository(
    @Autowired private val auctionRepository: AuctionRepository,
    @Autowired private val createAuctionEventPublisher: EventPublisher,
): DbAuctionRepository {

    override suspend fun create(auction: Auction): Long {
        val auctionEntity = auction.toAuctionEntity()
        val savedAuction = auctionRepository.save(auctionEntity).awaitSingleOrNull()
        auction.getEvents().forEach { createAuctionEventPublisher.publish(it) }
        return savedAuction?.id ?: throw IllegalStateException("Auction not saved")
    }

    override suspend fun get(id: Long) = auctionRepository.findById(id).awaitSingleOrNull()?.toAuction()

    override suspend fun save(auction: Auction) {
        auctionRepository.save(auction.toAuctionEntity()).awaitSingleOrNull()?.let {
            auction.getEvents().forEach(createAuctionEventPublisher::publish)
        }
    }

    override suspend fun getAll(list: List<Long>): Flow<Auction> {
        return auctionRepository.findAllById(list).map { it.toAuction() }.asFlow()
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
    bids = bids.map { Auction.Bid(it.bidder, it.amount, it.timestamp) }.toMutableList(),
    status = when (status) {
        AuctionEntity.Status.Active -> Auction.Status.Active
        AuctionEntity.Status.Won -> Auction.Status.Won
        AuctionEntity.Status.Expired -> Auction.Status.Expired
        AuctionEntity.Status.Canceled -> Auction.Status.Canceled
    },
    ownerAddress = ownerAddress
)
