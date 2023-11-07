package com.example.nftmarketplace.nft.alchemy.alchemy.data.bodyparams

import com.fasterxml.jackson.annotation.JsonProperty

data class BatchNFTs(
    @JsonProperty("tokens")
    val tokens: List<TokenInfo>,

    @JsonProperty("refreshCache")
    val refreshCache: Boolean = false
)
