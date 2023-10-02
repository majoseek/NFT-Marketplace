package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.storage.db.AuctionEntity
import com.example.nftmarketplace.auction.storage.db.AuctionRepository
import com.example.nftmarketplace.core.AuctionPort
import com.example.nftmarketplace.core.NFTPort
import com.example.nftmarketplace.core.auction.AuctionEvents
import com.example.nftmarketplace.core.data.AuctionDomainModel
import com.example.nftmarketplace.nft.storage.db.NFTEntity
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
    @Autowired private val nftPort: NFTPort
) : AuctionPort {
    override suspend fun getAllAuctions(
        page: Int,
        count: Int,
        status: AuctionDomainModel.Status?
    ): Flow<AuctionDomainModel> {
        val startIndex = ((page - 1) * count).toLong()
        return auctionRepository.findAllById(startIndex until (startIndex + count)).asFlow().map {
            val nft = nftPort.getNFT(it.nft.contractAddress, it.nft.tokenId.toString())
            AuctionDomainModel(
                auctionID = it.id,
                title = it.title,
                description = it.description,
                nft = NFTToken(
                    address = nft.contractAddress,
                    tokenID = nft.tokenID,
                    url = nft.url,
                    type = NFTEntity.Type.valueOf(nft.type.name),
                    ownerAddress = nft.ownerAddress,
                    name = nft.name.orEmpty(),
                    description = nft.description.orEmpty(),
                ),
                expiryTime = it.expiryTime,
                status = when (it.status) {
                    AuctionEntity.Status.NotStared -> AuctionDomainModel.Status.Pending
                    AuctionEntity.Status.Active -> AuctionDomainModel.Status.Active
                    AuctionEntity.Status.Cancelled -> AuctionDomainModel.Status.Cancelled
                    AuctionEntity.Status.Expired -> AuctionDomainModel.Status.Expired
                    AuctionEntity.Status.Won -> AuctionDomainModel.Status.Won
                },
                highestBid = it.currentBid?.let { bid ->
                    AuctionDomainModel.Bid(
                        bidder = bid.bidder,
                        amount = bid.amount,
                        timestamp = bid.timestamp
                    )
                },
                startingPrice = it.startingPrice,
                minimumIncrement = it.minimalIncrement,
            )
        }
    }

    override suspend fun getAuctionById(auctionId: Long): AuctionDomainModel {
        return auctionContract.getAuctionById(auctionId)
    }

    override suspend fun getAuctionByOwner(ownerAddress: String): List<AuctionDomainModel> {
        return auctionContract.getAuctionByOwner(ownerAddress)
    }

    override suspend fun getAuctionByStatus(status: AuctionDomainModel.Status): List<AuctionDomainModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getTotalAuctions(): Long {
        return auctionContract.getTotalAuctions()
    }

    override suspend fun getAuctionByNFT(contractAddress: String, tokenId: Long): AuctionDomainModel? {
        return auctionContract.getAuctionByNFT(contractAddress, tokenId)
    }

    override suspend fun getAuctionsByContract(contractAddress: String): List<AuctionDomainModel> {
        return auctionContract.getAuctionsByContract(contractAddress)
    }

    override suspend fun getAuctionsBids(auctionId: Long): Flow<AuctionDomainModel.Bid> {
        return auctionContract
            .getAuctionsEvents()
            .filterIsInstance<AuctionEvents.BidPlaced>()
            .filter { it.id == auctionId }
            .map {
                AuctionDomainModel.Bid(
                    bidder = it.bidderAddress,
                    amount = Convert.fromWei(it.amount.toString(), Convert.Unit.ETHER),
                    timestamp = it.timestamp
                )
            }
    }
}
