package com.example.nftmarketplace.nft.storage.db

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NFTRepository : ReactiveMongoRepository<NFTEntity, NFTId>
