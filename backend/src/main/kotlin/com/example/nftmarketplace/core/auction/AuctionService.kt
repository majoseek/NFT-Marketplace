package com.example.nftmarketplace.core.auction

import com.example.nftmarketplace.core.AuctionPort
import com.example.nftmarketplace.core.data.AuctionDomainModel
import com.example.nftmarketplace.getOrPrintError
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuctionService(
    @Autowired private val auctionPort: AuctionPort,
) {

    suspend fun getAllAuctions(page: Int, count: Int, status: AuctionDomainModel.Status? = null): Flow<AuctionDomainModel> =
        auctionPort.getAllAuctions(page, count, status)

    suspend fun getAuctionById(auctionId: Long): AuctionDomainModel? = getOrPrintError {
        auctionPort.getAuctionById(auctionId)
    }

    suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long? = null): List<AuctionDomainModel>? = getOrPrintError {
        tokenId?.let {
            auctionPort.getAuctionByNFT(contractAddress, tokenId)?.let { auction ->
                listOf(auction)
            }
        } ?: run {
            auctionPort.getAuctionsByContract(contractAddress)
        }
    }

    suspend fun getAuctionByOwner(ownerAddress: String): List<AuctionDomainModel>? = getOrPrintError {
        auctionPort.getAuctionByOwner(ownerAddress)
    }

    suspend fun getTotalAuctionsSize(): Long? = getOrPrintError {
        auctionPort.getTotalAuctions()
    }
}
