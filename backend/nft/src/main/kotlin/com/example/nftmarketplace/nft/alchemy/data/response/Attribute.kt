package com.example.nftmarketplace.nft.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class Attribute(
    @JsonProperty("trait_type")
    val traitType: String? = null,
    @JsonProperty("value")
    val value: String? = null
)
