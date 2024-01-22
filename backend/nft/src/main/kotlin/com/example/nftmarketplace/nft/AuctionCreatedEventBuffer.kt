package com.example.nftmarketplace.nft

import com.example.nftmarketplace.nft.requests.CreateNFTRequestHandler
import com.example.nftmarketplace.nft.requests.command.CreateNFTBatchCommand
import com.example.nftmarketplace.nft.requests.command.CreateNFTCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
@Component
class AuctionCreatedEventBuffer(
    private val requestHandler: CreateNFTRequestHandler,
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val buffer = ConcurrentLinkedQueue<CreateNFTCommand>()
    private val channel = Channel<Unit>(Channel.CONFLATED)

    init {
        coroutineScope.launch {
            launch { processBuffer() }
            channel.consumeAsFlow()
                .debounce(3.seconds)
                .collect { flushBuffer() }
        }
    }


    suspend fun add(command: CreateNFTCommand) {
        buffer.add(command)
        channel.send(Unit)
    }

    private suspend fun flushBuffer() {
        if (buffer.isNotEmpty()) {
            val commands = mutableListOf<CreateNFTCommand>()

            while (buffer.isNotEmpty()) {
                commands.add(buffer.poll())
                if (commands.size == com.example.nftmarketplace.nft.AuctionCreatedEventBuffer.Companion.CHUNK_SIZE) {
                    requestHandler.handleBatch(CreateNFTBatchCommand(commands))
                    commands.clear()
                }
            }

            when (commands.size) {
                1 -> requestHandler.handle(commands.first())
                else -> requestHandler.handleBatch(CreateNFTBatchCommand(commands))
            }
        }
    }

    private suspend fun processBuffer() {
        while (coroutineScope.isActive) {
            delay(2.seconds)
            flushBuffer()
        }
    }

    companion object {
        val CHUNK_SIZE = 100
    }
}