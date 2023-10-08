package com.example.nftmarketplace.restapi.auctions

import com.fasterxml.jackson.annotation.JsonProperty

enum class AuctionStatus {
    @JsonProperty("active")
    Active,
    @JsonProperty("expired")
    Expired,
    @JsonProperty("cancelled")
    Cancelled,
    @JsonProperty("ending")
    Ending,
    @JsonProperty("completed")
    Completed,
    @JsonProperty("not_started")
    NotStarted,
}
