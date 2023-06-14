package com.example.nftmarketplace.auction

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

//  auctionRecordId   uint256 :  0
//  title   string :
//  description   string :
//  assetAddress   address :  0x0000000000000000000000000000000000000000
//  assetRecordId   uint256 :  0
//  startingPrice   uint128 :  0
//  reservePrice   uint128 :  0
//  minimumIncrement   uint128 :  0
//  distributionCut   uint8 :  0
//  expiryTime   uint256 :  0
//  status   uint8 :  0
//  sellerAddress   address :  0x0000000000000000000000000000000000000000
//  highestBid   tuple :  0x0000000000000000000000000000000000000000,0
data class NFTAuctionObject(
    val auctionID: Long,
    val title: String,
    val description: String,
    val nft: NFTToken,
    val startingPrice: BigDecimal,
    val reservePrice: BigDecimal,
    val minimumIncrement: BigDecimal,
    val expiryTime: LocalDateTime,
    val bids: List<Bid>? = null,
    val highestBid: Bid? = null,
    val status: Status,
) {
    enum class Status {
        Pending, // 0
        Active, // 1
        Cancelled, // 2
    }
    data class Bid(
        val bidder: String,
        val amount: BigDecimal,
    )

}
