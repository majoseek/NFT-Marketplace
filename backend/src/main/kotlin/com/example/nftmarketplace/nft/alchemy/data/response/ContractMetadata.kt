package com.example.nftmarketplace.nft.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class ContractMetadata(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("openSea")
    val openSea: OpenSea,
    @JsonProperty("symbol")
    val symbol: String,
    @JsonProperty("tokenType")
    val tokenType: String,
    @JsonProperty("totalSupply")
    val totalSupply: String
)
