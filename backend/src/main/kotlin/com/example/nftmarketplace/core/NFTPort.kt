package com.example.nftmarketplace.core

import com.example.nftmarketplace.core.data.NFTDomainModel

interface NFTPort {

    suspend fun getNFT(contractAddress: String, tokenId: String, withOwner: Boolean = false): NFTDomainModel

    suspend fun getOwnedNFTs(ownerAddress: String): List<NFTDomainModel>

    suspend fun getNFTs(contractAddress: String, ownerAddress: String? = null): List<NFTDomainModel>

    suspend fun getOrCreateNFT(contractAddress: String, tokenId: String): NFTDomainModel
}
