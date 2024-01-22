package com.example.nftmarketplace.nft.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class ContractMetadata(
    @JsonProperty("name")
    val name: String? = null,
    @JsonProperty("symbol")
    val symbol: String? = null,
    @JsonProperty("tokenType")
    val tokenType: String? = null,
    @JsonProperty("totalSupply")
    val totalSupply: String? = null
)
