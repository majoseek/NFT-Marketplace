package com.example.nftmarketplace.auction.storage.db

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AuctionRepository : ReactiveMongoRepository<AuctionEntity, Long>
