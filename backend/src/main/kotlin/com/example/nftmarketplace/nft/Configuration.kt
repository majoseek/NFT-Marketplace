package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.alchemy.AlchemyAPIAdapter
import com.example.nftmarketplace.nft.storage.db.NFTRepository
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration("NFTAdapterConfiguration")
class Configuration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun createNFTRequestHandler(
        alchemyAPIAdapter: AlchemyAPIAdapter,
        nftRepository: NFTRepository
    ): CreateNFTRequestHandler {
        return CrateNFTRequestHandlerImpl(alchemyAPIAdapter, nftRepository)
    }
}
