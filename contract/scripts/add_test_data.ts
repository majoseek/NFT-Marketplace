import {ethers} from "hardhat";
import {HardhatEthersSigner} from "@nomicfoundation/hardhat-ethers/signers";
import {randomInt, sign} from "node:crypto";


// Your Ethereum wallet private key (keep it secret)

// The address of the deployed contract
const contractAddress = "0xBC6386652f66d30Fd5fF39E14AE58FE3019B10Da"; // Replace with your contract's address

// The ABI (Application Binary Interface) of your contract
const contractABI = [
    "function mintNFT(string memory _tokenURI) public"
];

async function setApprovalForAll(
    signer: HardhatEthersSigner,
) {

    const myContract = await ethers.getContractAt(
        "TestNFT",
        "0x641FD66C35EEe187843cf7F109424E1f7cd563c1",
        signer
    )

    const tx = await myContract.setApprovalForAll(
        contractAddress,
        true,
        {gasLimit: 500000}
    );
}


async function createAuction(
    signer: HardhatEthersSigner,
    assetAddress: string,
    tokenId: string,
) {
        const myContract = await ethers.getContractAt(
            "NFTAuction",
            contractAddress,
            signer
        )

        const tx = await myContract.connect(signer).createAuction(
                "Example Auction Title",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                assetAddress,
                tokenId,
                1,
                randomInt(1, 100),
                1,
                randomInt(8600, 180000)
            );
        console.log("Transaction hash:", tx.hash);

        // Wait for the transaction to be confirmed
        const receipt = await tx.wait();
        console.log("Transaction confirmed in block:", receipt?.blockNumber);
}


async function main(): Promise<void> {
    await setApprovalForAll((await ethers.getSigners())[0])
    for (let i = 1; i < 40; i++) {
        await createAuction(
            (await ethers.getSigners())[0],
            "0x641FD66C35EEe187843cf7F109424E1f7cd563c1",
            i.toString()
        )
    }
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });
