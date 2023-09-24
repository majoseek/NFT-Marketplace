package com.example.nftmarketplace.auction.nftauctioncontract

import com.example.nftmarketplace.core.AuctionPort
import com.example.nftmarketplace.nftauction.NFTAuction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.DefaultGasProvider

@Configuration
class NFTAuctionContractConfiguration {

    @Bean
    fun web3j(
        @Value("\${alchemy.api.url}") alchemyApiUrl: String,
        @Value("\${alchemy.api.key}") alchemyApiKey: String,
    ): Web3j = Web3j.build(HttpService("$alchemyApiUrl/v2/$alchemyApiKey"))


    @Bean
    fun getNFTAuction(
        @Value("\${contract.address}") contractAddress: String,
        @Autowired web3j: Web3j,
    ): NFTAuction = NFTAuction.load(
        contractAddress,
        web3j,
        ReadonlyTransactionManager(web3j, contractAddress),
        DefaultGasProvider()
    )
}
