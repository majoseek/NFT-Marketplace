package com.example.nftmarketplace.restapi.auctions

import java.math.BigDecimal

data class BidElement(
    val bidder: String,
    val amount: BigDecimal,
    val timestamp: String,
)

