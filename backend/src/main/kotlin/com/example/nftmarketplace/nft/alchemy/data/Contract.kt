package com.example.nftmarketplace.nft.alchemy.data


import com.fasterxml.jackson.annotation.JsonProperty

data class Contract(
    @JsonProperty("address")
    val address: String
)
