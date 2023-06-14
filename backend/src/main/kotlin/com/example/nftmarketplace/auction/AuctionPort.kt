package com.example.nftmarketplace.auction

interface AuctionPort {
    suspend fun getAllAuctions(page: Int, count: Int): List<NFTAuctionObject>

    suspend fun getAuctionById(auctionId: Long): NFTAuctionObject

    suspend fun getAuctionByOwner(ownerAddress: String): List<NFTAuctionObject>

    suspend fun getAuctionByStatus(status: NFTAuctionObject.Status): List<NFTAuctionObject>

    suspend fun getTotalAuctions(): Long

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): NFTAuctionObject?

    suspend fun getAuctionsByContract(contractAddress: String): List<NFTAuctionObject>
}
