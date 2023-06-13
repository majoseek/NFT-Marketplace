package com.example.nftmarketplace.auction

import java.math.BigInteger

data class CreateAuctionRequest(
    val name: String,
    val description: String,
    val assetAddress: String,
    val assetRecordId: BigInteger,
    val startingPrice: BigInteger = BigInteger.valueOf(1),
    val maximumPrice: BigInteger = BigInteger.valueOf(Long.MAX_VALUE),
    val minimumIncrement: BigInteger = BigInteger.valueOf(100),
    val duration: BigInteger = BigInteger.valueOf(86400),
    val distributionCut: BigInteger = BigInteger.valueOf(5)
)

//@RequestParam name: String,
//        @RequestParam description: String,
//        @RequestParam assetAddress: String,
//        @RequestParam assetRecordId: BigInteger,
//        @RequestParam startingPrice: BigInteger,
//        @RequestParam maximumPrice: BigInteger,
//        @RequestParam minimumIncrement: BigInteger,
//        @RequestParam duration: BigInteger,
//        @RequestParam distributionCut: BigInteger
