package com.example.nftmarketplace.nft

interface NFTPort {

    suspend fun getNFT(contractAddress: String, tokenId: String): NFT

    suspend fun getOwnedNFTs(ownerAddress: String): List<NFT>

    suspend fun getNFTs(contractAddress: String, ownerAddress: String? = null): List<NFT>
}
