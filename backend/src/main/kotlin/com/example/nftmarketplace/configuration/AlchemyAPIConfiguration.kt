package com.example.nftmarketplace.configuration

import com.example.nftmarketplace.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient


@Configuration(value = "alchemyAPIConfiguration")
class AlchemyAPIConfiguration {

    @Bean
    fun webClient(
        @Value("\${alchemy.api.url}") url: String,
        @Value("\${alchemy.api.key}") key: String,
    ) = WebClient.builder()
        .baseUrl("$url/nft/v2/$key/")
        .codecs {
            it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder())
            it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder())
        }
        .filter { request, next ->
            getLogger().info(this::class.simpleName, "Request: ${request.method()} ${request.url()}")
            next.exchange(request)
                .doOnNext { response ->
                    getLogger().info(this::class.simpleName, "Response: ${response.statusCode()}")
                }
        }
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "application/json")
        .build()
}
