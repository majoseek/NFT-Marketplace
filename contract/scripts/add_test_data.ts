import { ethers } from "hardhat";
import { readFile, readFileSync } from "fs";
import { BigNumber, Contract, ethers } from "ethers";
import { randomInt } from "crypto";

const CONTRACT_PATH = "./artifacts/contracts/contract.sol/NFTAuction.json";

interface CreateAuction {
    name: string,
    description: string,
    assetContractAddress: string,
    tokenId: string,
    duration: BigNumber,
    reservePrice: BigNumber,
    startPrice: BigNumber,
    minimumBidIncrement: BigNumber,
}

const tokenAddresses = "0xE49E3638Ef0411F10B311451D277904C8C69eebd"

async function main(): Promise<void> {

    const contractAddress = "0x534ED011c1F3DdEe3585FB1583e3BfBabaA75BF0";
    const contractArtifact = JSON.parse(readFileSync(CONTRACT_PATH, 'utf8'));

    const { abi } = contractArtifact;

    const createAuctionPrivateKey = process.env.CREATE_AUCTION_WALLET;
    const placeBidWalletPrivateKey = process.env.PLACE_BID_WALLET;
    if (!createAuctionPrivateKey || !placeBidWalletPrivateKey) {
        throw new Error("Please set your private keys of accounts in your environment.");
    }

    const provider = new ethers.providers.JsonRpcProvider(process.env.SEPOLIA_API_URL);
    const createAuctionWallet = new ethers.Wallet(createAuctionPrivateKey, provider);
    const placeBidWallet = new ethers.Wallet(placeBidWalletPrivateKey, provider);

    const contractCreate = new ethers.Contract(
        contractAddress,
        abi,
        createAuctionWallet
    );

    const contractPlaceBid = new ethers.Contract(
        contractAddress,
        abi,
        placeBidWallet
    );

    for (let i = 27; i < 28; i++) {
        const auctionParams: CreateAuction =  {
            name: "Test auction #" + i,
            description: "Test description #" + i,
            assetContractAddress: tokenAddresses,
            tokenId: i.toString(),
            duration: ethers.BigNumber.from(randomInt(10000, 1000000)),
            reservePrice: ethers.BigNumber.from('0x1'),
            startPrice: ethers.BigNumber.from('0x1'),
            minimumBidIncrement: ethers.BigNumber.from('0x1'),
        }
        console.log("Create auction...",)
        const tx = await contractCreate["createAuction"](
            auctionParams.name,
            auctionParams.description,
            auctionParams.assetContractAddress,
            auctionParams.tokenId,
            auctionParams.startPrice,
            auctionParams.reservePrice,
            auctionParams.minimumBidIncrement,
            auctionParams.duration,
            {gasLimit: 5000000} 
        )
        await tx.wait()
        console.log("Transaction hash: ", tx.hash)
        const auctionId = await contractCreate.auctionCount()
        console.log("Auction created: ", auctionId)

        console.log("Place bid on auction: ", i)
        for (let j = 0; j < 5; j++) {
            const tx = await placeBid(contractPlaceBid, i, 10 + j)
            sleepBlocking(30 * 1000)
        }        
    }
}


const placeBid = async (
    contract: Contract,
    auctionId: number,
    bidAmount: number,
) => {
    const tx = await contract.placeBid(BigNumber.from(auctionId), { value: bidAmount, gasLimit: 5000000 })
    const receipt = tx.wait(5)
    if (receipt.status === 0) {
        throw new Error("Transaction failed")
    } else {
        console.log("Bid placed on Auction ID: ", auctionId)
    }
}

function sleepBlocking(milliseconds: number) {
    const start = new Date().getTime();
    while (new Date().getTime() - start < milliseconds) {
        // Loop until the time has passed
    }
}


main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });
