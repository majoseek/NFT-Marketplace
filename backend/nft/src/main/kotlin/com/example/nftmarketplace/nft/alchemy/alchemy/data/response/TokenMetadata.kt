package com.example.nftmarketplace.nft.alchemy.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class TokenMetadata(
    @JsonProperty("tokenType")
    val tokenType: String
)
