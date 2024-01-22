package com.example.nftmarketplace.nft

import com.example.nftmarketplace.restapi.nfts.NFTResponse

interface NFTQuery {

    suspend fun getNFT(contractAddress: String, tokenId: Long, withOwner: Boolean = false): NFTResponse

    suspend fun getOwnedNFTs(ownerAddress: String): List<NFTResponse>

    suspend fun getNFTs(contractAddress: String, ownerAddress: String? = null): List<NFTResponse>
}
