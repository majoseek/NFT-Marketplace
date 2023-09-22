package com.example.nftmarketplace.nft.alchemy.data


import com.fasterxml.jackson.annotation.JsonProperty

data class AlchemyNFT(
    @JsonProperty("contract")
    val contract: Contract,
    @JsonProperty("contractMetadata")
    val contractMetadata: ContractMetadata? = null,
    @JsonProperty("description")
    val description: String,
    @JsonProperty("id")
    val id: Id,
    @JsonProperty("media")
    val media: List<Media>,
    @JsonProperty("metadata")
    val metadata: Metadata,
    @JsonProperty("timeLastUpdated")
    val timeLastUpdated: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("tokenUri")
    val tokenUri: TokenUri
)
