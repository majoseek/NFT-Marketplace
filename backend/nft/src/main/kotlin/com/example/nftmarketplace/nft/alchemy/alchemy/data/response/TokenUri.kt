package com.example.nftmarketplace.nft.alchemy.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class TokenUri(
    @JsonProperty("gateway")
    val gateway: String,
    @JsonProperty("raw")
    val raw: String
)
