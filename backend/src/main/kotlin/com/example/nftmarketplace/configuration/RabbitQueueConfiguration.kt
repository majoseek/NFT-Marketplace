package com.example.nftmarketplace.configuration

import jakarta.annotation.PostConstruct
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitQueueConfiguration(
    @Autowired private val amqpAdmin: AmqpAdmin,
) {

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory) = RabbitTemplate(connectionFactory).apply {
        messageConverter = Jackson2JsonMessageConverter()
    }

    @PostConstruct
    fun init() {
        with(amqpAdmin) {
            val auctionCreatedQueue = Queue(AUCTION_CREATED_QUEUE, false, false, false, emptyMap())
            val mainExchange = DirectExchange(EXCHANGE)
            declareQueue(auctionCreatedQueue)
            declareExchange(mainExchange)
            declareBinding(BindingBuilder.bind(auctionCreatedQueue).to(mainExchange).with(AUCTION_CREATED_ROUTING_KEY))
        }
    }


    companion object {
        const val AUCTION_BID_PLACED_ROUTING_KEY = "auctions.bid-placed"
        const val AUCTION_EXTENDED_ROUTING_KEY = "auctions.extended"
        const val AUCTION_WON_ROUTING_KEY = "nft.transferred"
        const val EXCHANGE = "nft-auctions"
        const val AUCTION_CREATED_QUEUE = "auction-created"
        const val AUCTION_CREATED_ROUTING_KEY = "auctions.new"
    }
}
