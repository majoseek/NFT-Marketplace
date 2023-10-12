package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.nftauctioncontract.AuctionEvents
import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import com.example.nftmarketplace.nft.NFTQuery
import com.example.nftmarketplace.restapi.auctions.AuctionResponse
import com.example.nftmarketplace.restapi.auctions.AuctionStatus
import com.example.nftmarketplace.restapi.auctions.AuctionsPagedResponse
import com.example.nftmarketplace.restapi.auctions.BidElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.web3j.utils.Convert

@Component
class AuctionAdapter(
    @Autowired private val auctionContract: ContractHelper,
    @Autowired private val auctionRepository: DbAuctionRepository,
    @Autowired private val nftQuery: NFTQuery,
) : AuctionQuery {

    override suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: AuctionStatus?,
    ): Flow<AuctionsPagedResponse.AuctionElement> {
        val startIndex = ((page - 1) * count).toLong()
        return auctionRepository.getAll((startIndex until (startIndex + count)).toList()).map { auction ->
            val nft = nftQuery.getNFT(auction.nft.contractAddress, auction.nft.tokenId)
            AuctionsPagedResponse.AuctionElement(
                auctionID = auction.auctionId,
                title = auction.title,
                description = auction.description,
                nft = nft,
                expiryTime = auction.expiryTime.toString(),
                highestBid = auction.bids.lastOrNull()
                    ?.let { BidElement(it.bidder, it.amount, it.timestamp.toString()) },
                status = AuctionStatus.valueOf(auction.status.name),
            )
        }
    }

    override suspend fun getAuctionById(auctionId: Long): AuctionResponse? =
        auctionContract.getAuctionById(auctionId)?.let {
            return it.toAuctionResponse(nftQuery.getNFT(it.nft.contractAddress, it.nft.tokenId))
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

