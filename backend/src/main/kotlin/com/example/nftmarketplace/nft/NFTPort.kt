package com.example.nftmarketplace.nft

interface NFTPort {

    suspend fun getNFT(contractAddress: String, tokenId: String, withOwner: Boolean = false): NFT

    suspend fun getOwnedNFTs(ownerAddress: String): List<NFT>

    suspend fun getNFTs(contractAddress: String, ownerAddress: String? = null): List<NFT>
}
