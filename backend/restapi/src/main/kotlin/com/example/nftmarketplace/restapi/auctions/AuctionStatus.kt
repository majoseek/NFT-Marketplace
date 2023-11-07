package com.example.nftmarketplace.restapi.auctions

import com.fasterxml.jackson.annotation.JsonProperty

enum class AuctionStatus {
    @JsonProperty("active")
    Active,
    @JsonProperty("expired")
    Expired,
    @JsonProperty("cancelled")
    Canceled,
    @JsonProperty("ending")
    Ending,
    @JsonProperty("completed")
    Completed,
}
