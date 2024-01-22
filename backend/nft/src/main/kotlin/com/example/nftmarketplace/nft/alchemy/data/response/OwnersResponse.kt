package com.example.nftmarketplace.nft.alchemy.data.response

import com.fasterxml.jackson.annotation.JsonProperty

data class OwnersResponse(
    @JsonProperty("owners")
    val owners: List<String>
)
