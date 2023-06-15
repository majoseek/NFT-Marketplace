package com.example.nftmarketplace.nft.alchemy.data

import com.fasterxml.jackson.annotation.JsonProperty

data class OwnersResponse(
    @JsonProperty("owners")
    val owners: List<String>
)
