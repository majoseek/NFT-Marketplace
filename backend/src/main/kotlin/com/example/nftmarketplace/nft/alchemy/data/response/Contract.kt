package com.example.nftmarketplace.nft.alchemy.data.response


import com.fasterxml.jackson.annotation.JsonProperty

data class Contract(
    @JsonProperty("address")
    val address: String
)
