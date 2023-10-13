package com.example.nftmarketplace.nft.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class TokenMetadata(
    @JsonProperty("tokenType")
    val tokenType: String
)
