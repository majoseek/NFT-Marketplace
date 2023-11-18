package com.example.nftmarketplace.auction

import com.example.nftmarketplace.auction.nftauctioncontract.AuctionEvents
import com.example.nftmarketplace.auction.nftauctioncontract.ContractHelper
import com.example.nftmarketplace.auction.storage.db.DbAuctionRepository
import com.example.nftmarketplace.restapi.auctions.BidElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.web3j.utils.Convert

@Component
class AuctionAdapter(
    @Autowired private val auctionContract: ContractHelper,
    @Autowired private val dbAuctionRepository: DbAuctionRepository,
) : AuctionQuery {
    override suspend fun getAuctionsBids(auctionId: Long): Flow<List<BidElement>> {
        val initialFlow = flowOf(dbAuctionRepository.get(auctionId)?.bids?.map {
            BidElement(
                bidder = it.bidder,
                amount = it.amount,
                timestamp = it.timestamp.toString()
            )
        }.orEmpty())

        return merge(initialFlow, auctionContract
            .getAuctionsEvents()
            .filterIsInstance<AuctionEvents.BidPlaced>()
            .filter { it.id == auctionId }
            .map {
                listOf(
                    BidElement(
                        bidder = it.bidderAddress,
                        amount = Convert.fromWei(it.amount.toString(), Convert.Unit.ETHER),
                        timestamp = it.timestamp.toString()
                    )
                )
            }
        )
    }
}

