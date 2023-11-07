package com.example.nftmarketplace.nft.alchemy.alchemy.data.bodyparams

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenInfo(
    @JsonProperty("contractAddress")
    val contractAddress: String,

    @JsonProperty("tokenId")
    val tokenId: Long
)
