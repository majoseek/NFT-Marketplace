package com.example.nftmarketplace.auction

import com.example.nftmarketplace.getOrPrintError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuctionService(@Autowired private val auctionPort: AuctionPort) {

    suspend fun getAllAuctions(page: Int, count: Int, status: NFTAuctionObject.Status? = null): List<NFTAuctionObject>? = getOrPrintError {
        auctionPort.getAllAuctions(page, count, status)
    }

    suspend fun getAuctionById(auctionId: Long): NFTAuctionObject? = getOrPrintError {
        auctionPort.getAuctionById(auctionId)
    }

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long? = null): List<NFTAuctionObject>? = getOrPrintError {
        tokenId?.let {
            auctionPort.getAuctionByNFT(contractAddress, tokenId)?.let { auction ->
                listOf(auction)
            }
        } ?: run {
            auctionPort.getAuctionsByContract(contractAddress)
        }
    }

    suspend fun getAuctionByOwner(ownerAddress: String): List<NFTAuctionObject>? = getOrPrintError {
        auctionPort.getAuctionByOwner(ownerAddress)
    }

    suspend fun getTotalAuctions(): Long? = getOrPrintError {
        auctionPort.getTotalAuctions()
    }
}
