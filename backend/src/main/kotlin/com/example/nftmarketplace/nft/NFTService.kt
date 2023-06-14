package com.example.nftmarketplace.nft

import com.example.nftmarketplace.getOrPrintError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NFTService(@Autowired private val nftPort: NFTPort) {

    // TODO add some logic, caching etc


    suspend fun getNFT(contractAddress: String, tokenId: String) = getOrPrintError {
        nftPort.getNFT(contractAddress, tokenId)
    }

    suspend fun getOwnedNFTs(ownerAddress: String) = getOrPrintError {
        nftPort.getOwnedNFTs(ownerAddress)
    }

    suspend fun getNFTs(contractAddress: String, ownerAddress: String? = null) = getOrPrintError {
        nftPort.getNFTs(contractAddress, ownerAddress)
    }
}
