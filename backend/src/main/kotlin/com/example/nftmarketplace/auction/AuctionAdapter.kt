package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.storage.db.AuctionRepository
import com.example.nftmarketplace.core.auction.AuctionEvents
import com.example.nftmarketplace.nft.NFTQuery
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import com.example.nftmarketplace.restapi.auctions.BidElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.web3j.utils.Convert

@Component
class AuctionAdapter(
    @Autowired private val auctionContract: ContractHelper,
    @Autowired private val auctionRepository: AuctionRepository,
    @Autowired private val nftQuery: NFTQuery,
) : AuctionQuery {
    override suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: AuctionStatus?,
    ): Flow<AuctionsPagedResponse.AuctionElement> {
        val startIndex = ((page - 1) * count).toLong()
        val totalSize = auctionContract.getTotalAuctions()
        return auctionRepository.findAllById(startIndex until (startIndex + count)).asFlow().map {
            val nft = nftQuery.getNFT(it.nft.contractAddress, it.nft.tokenId)
            it.toAuctionItem(nft)
        }
    }

    override suspend fun getAuctionById(auctionId: Long): AuctionResponse {
        return auctionContract.getAuctionById(auctionId).toAuctionResponse(
            nftQuery.getNFT(
                contractAddress = auctionContract.getAuctionById(auctionId).nft.contractAddress,
                tokenId = auctionContract.getAuctionById(auctionId).nft.tokenId
            )
        )
    }

    override suspend fun getAuctionByOwner(ownerAddress: String): List<AuctionResponse> {
        return auctionContract.getAuctionByOwner(ownerAddress).map {
            it.toAuctionResponse(
                nftQuery.getNFT(
                    contractAddress = it.nft.contractAddress,
                    tokenId = it.nft.tokenId
                )
            )
        }
    }

    override suspend fun getAuctionByStatus(status: AuctionStatus): List<AuctionResponse> {
        TODO()
    }

    override suspend fun getTotalAuctions(): Long {
        return auctionContract.getTotalAuctions()
    }

    override suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionResponse? {
        return auctionContract.getAuctionByNFT(contractAddress, tokenId).toAuctionResponse(null)
    }

    override suspend fun getAuctionsByContract(contractAddress: String): List<AuctionResponse> {
        return auctionContract.getAuctionsByContract(contractAddress).map { it.toAuctionResponse() }
    }

    override suspend fun getAuctionsBids(auctionId: Long): Flow<BidElement> {
        return auctionContract
            .getAuctionsEvents()
            .filterIsInstance<AuctionEvents.BidPlaced>()
            .filter { it.id == auctionId }
            .map {
                BidElement(
                    bidder = it.bidderAddress,
                    amount = Convert.fromWei(it.amount.toString(), Convert.Unit.ETHER),
                    timestamp = it.timestamp.toString()
                )
            }
    }
}

