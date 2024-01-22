package com.example.nftmarketplace.nft

import com.example.nftmarketplace.common.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.util.retry.Retry
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


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
            getLogger().info("Request: ${request.method()} ${request.url()}")
            next.exchange(request)
                .doOnError {
                    getLogger().error("Error: ${it.message}")
                }
                .doOnNext { response ->
                    getLogger().info("Response: ${response.statusCode()}")
                }.retryWhen(
                    Retry.backoff(10, 1.seconds.toJavaDuration())
                )
        }
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Accept", "application/json")
        .build()
}
