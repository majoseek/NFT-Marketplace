package com.example.nftmarketplace.events

import com.example.nftmarketplace.common.EventPublisher
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_BID_PLACED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_BID_PLACED_ROUTING_KEY
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_COMPLETED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_COMPLETED_ROUTING_KEY
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_CREATED_PROJECTED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_CREATED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_CREATED_ROUTING_KEY
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_EXTENDED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.AUCTION_EXTENDED_ROUTING_KEY
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.EXCHANGE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.NFT_CREATED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.NFT_CREATED_ROUTING_KEY
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.NFT_TRANSFERRED_QUEUE
import com.example.nftmarketplace.events.RabbitQueueConfiguration.Companion.NFT_TRANSFERRED_ROUTING_KEY
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Configuration
class RabbitQueueConfiguration {

    @Bean
    fun rabbitListenerContainerFactory(
        connectionFactory: ConnectionFactory,
        rabbitTemplate: RabbitTemplate
    ): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(rabbitTemplate.messageConverter)
        return factory
    }

    @Bean
    @Primary
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        objectMapper: ObjectMapper
    ) = RabbitTemplate(connectionFactory).apply {
        messageConverter = Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun eventPublisher(
        @Autowired rabbitTemplate: RabbitTemplate
    ): EventPublisher = RabbitEventPublisher(rabbitTemplate)


    companion object {
        const val AUCTION_CREATED_ROUTING_KEY = "auctions.new"
        const val AUCTION_BID_PLACED_ROUTING_KEY = "auctions.bid-placed"
        const val AUCTION_EXTENDED_ROUTING_KEY = "auctions.extended"
        const val AUCTION_COMPLETED_ROUTING_KEY = "auctions.completed"
        const val NFT_TRANSFERRED_ROUTING_KEY = "nfts.transferred"
        const val NFT_CREATED_ROUTING_KEY = "nfts.new"
        const val EXCHANGE = "nft-auctions"
        const val AUCTION_CREATED_QUEUE = "auction-created"
        const val AUCTION_CREATED_PROJECTED_QUEUE = "auction-created-projected"
        const val AUCTION_BID_PLACED_QUEUE = "bid-placed"
        const val AUCTION_EXTENDED_QUEUE = "auction-extended"
        const val AUCTION_COMPLETED_QUEUE = "auction-completed"
        const val NFT_TRANSFERRED_QUEUE = "nft-transferred"
        const val NFT_CREATED_QUEUE = "nft-created"
    }
}

@Component
class RabbitInitializer(
    @Autowired private val amqpAdmin: AmqpAdmin,
) {

    @PostConstruct
    private fun rabbitInitializer() {
        val queues = listOf(
            AUCTION_CREATED_QUEUE to AUCTION_CREATED_ROUTING_KEY,
            AUCTION_BID_PLACED_QUEUE to AUCTION_BID_PLACED_ROUTING_KEY,
            AUCTION_EXTENDED_QUEUE to AUCTION_EXTENDED_ROUTING_KEY,
            AUCTION_COMPLETED_QUEUE to AUCTION_COMPLETED_ROUTING_KEY,
            NFT_TRANSFERRED_QUEUE to NFT_TRANSFERRED_ROUTING_KEY,
            NFT_CREATED_QUEUE to NFT_CREATED_ROUTING_KEY,
            AUCTION_CREATED_PROJECTED_QUEUE to AUCTION_CREATED_ROUTING_KEY,
        )
        // Check if exchange exists
        amqpAdmin.declareQueue()
        with(amqpAdmin) {
            declareExchange(DirectExchange(EXCHANGE))
            queues.forEach {
                val queue = Queue(it.first, false, false, false, emptyMap())
                declareQueue(queue)
                declareBinding(
                    Binding(
                        queue.name,
                        Binding.DestinationType.QUEUE,
                        EXCHANGE,
                        it.second,
                        emptyMap()
                    )
                )
            }
        }
    }
}
