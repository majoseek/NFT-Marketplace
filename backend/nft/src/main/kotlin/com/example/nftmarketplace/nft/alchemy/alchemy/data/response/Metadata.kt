package com.example.nftmarketplace.nft.alchemy.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class Metadata(
    @JsonProperty("attributes")
    val attributes: List<Attribute>,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("image")
    val image: String,
    @JsonProperty("name")
    val name: String
)
