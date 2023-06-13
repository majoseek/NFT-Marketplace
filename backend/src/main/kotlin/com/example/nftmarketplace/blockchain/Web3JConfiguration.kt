package com.example.nftmarketplace.blockchain

import com.example.nftmarketplace.nftauction.NFTAuction
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider

@Configuration
class Web3JConfiguration {

    @Value("\${alchemy.api.url}")
    val alchemyApiUrl: String? = null

    @Value("\${alchemy.api.key}")
    val alchemyApiKey: String? = null

    @Value("\${contract.address}")
    val contractAddress: String? = null

    @Bean
    fun web3j(): Web3j {
        return Web3j.build(HttpService("$alchemyApiUrl/v2/$alchemyApiKey"))
    }

    @Bean
    fun contract(): NFTAuction {
        return NFTAuction.load(contractAddress, web3j(), Credentials.create(""), DefaultGasProvider())
    }
}
