package com.example.nftmarketplace.nft.alchemy.data


import com.fasterxml.jackson.annotation.JsonProperty

data class Attribute(
    @JsonProperty("trait_type")
    val traitType: String,
    @JsonProperty("value")
    val value: String
)
