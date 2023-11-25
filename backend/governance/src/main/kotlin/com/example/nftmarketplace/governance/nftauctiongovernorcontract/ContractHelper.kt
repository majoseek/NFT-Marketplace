package com.example.nftmarketplace.governance.nftauctiongovernorcontract

import com.example.nftmarketplace.nftauctiongovernor.NFTAuctionGovernor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j

@Component
class ContractHelper(
    @Autowired private val contract: NFTAuctionGovernor,
    @Autowired private val web3j: Web3j,
) {
    private val scope = CoroutineScope(Dispatchers.IO) + SupervisorJob()


    suspend fun getAllProposals() {
        contract
    }
}
