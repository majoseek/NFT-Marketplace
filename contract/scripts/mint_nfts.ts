import {ethers} from "hardhat";


// Your Ethereum wallet private key (keep it secret)
const privateKey = "c69359e5edd950f2d638b6e5cd902255fc8a15f371a928ac7188638302e912b5"; // Replace with your private key

// The address of the deployed contract
const contractAddress = "0xE49E3638Ef0411F10B311451D277904C8C69eebd"; // Replace with your contract's address

// The ABI (Application Binary Interface) of your contract
const contractABI = [
    "function mintNFT(string memory _tokenURI) public"
];


async function mintNFT(tokenURI: string) {

    const [wallet] = await ethers.getSigners()

    const myContract = await ethers.getContractAt(
        "TestNFT",
        "0x641FD66C35EEe187843cf7F109424E1f7cd563c1",
        wallet
    )
    const tx = await myContract.safeMint(
        wallet.address,
        tokenURI,
        {gasLimit: 500000}
    );
    console.log("Transaction hash:", tx.hash);

    // Wait for the transaction to be confirmed
    const receipt = await tx.wait();
    console.log("Transaction confirmed in block:", receipt?.blockNumber);
}

async function main(): Promise<void> {
    for (let i = 1; i < 40; i++) {
        const tokenURI = "https://wkol.github.io/nft-metadatas/nft_metadata_" + i + ".json";
        console.log("Minting NFT with tokenURI:", tokenURI);
        await mintNFT(tokenURI);
    }
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });
