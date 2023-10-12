package com.example.nftmarketplace.core

import com.example.nftmarketplace.core.data.DomainEvent

fun interface EventPublisher {
    fun publish(event: DomainEvent)
}
