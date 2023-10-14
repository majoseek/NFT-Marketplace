package com.example.nftmarketplace.nft.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class Id(
    @JsonProperty("tokenId")
    val tokenId: String,
    @JsonProperty("tokenMetadata")
    val tokenMetadata: TokenMetadata
)
