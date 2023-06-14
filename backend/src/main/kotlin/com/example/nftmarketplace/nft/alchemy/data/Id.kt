package com.example.nftmarketplace.nft.alchemy.data


import com.fasterxml.jackson.annotation.JsonProperty

data class Id(
    @JsonProperty("tokenId")
    val tokenId: String,
    @JsonProperty("tokenMetadata")
    val tokenMetadata: TokenMetadata
)
