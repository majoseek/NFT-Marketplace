package com.example.nftmarketplace.common

import com.example.nftmarketplace.common.data.DomainEvent

fun interface EventPublisher {
    fun publish(event: DomainEvent)
}
