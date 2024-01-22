package com.example.nftmarketplace.auction.utils

import com.example.nftmarketplace.auction.Auction
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class AuctionTestDbRepository() : DbAuctionRepository {
    val auctions = mutableListOf<Auction>()

    override suspend fun create(auction: Auction): Long {
        auctions.add(auction)
        return auction.auctionId
    }

    override suspend fun get(id: Long): Auction? {
        return auctions.find { it.auctionId == id }
    }

    override suspend fun save(auction: Auction) {
        val oldIndex = auctions.indexOfFirst { it.auctionId == auction.auctionId }
        auctions[oldIndex] = auction
    }

    override suspend fun getAll(list: List<Long>): Flow<Auction> {
        return auctions.filter { list.contains(it.auctionId) }.asFlow()
    }
}
