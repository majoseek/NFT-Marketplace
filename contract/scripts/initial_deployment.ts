// import { HardhatRuntimeEnvironment } from "hardhat/types";
// import { ContractFactory } from "ethers";
// import hre from 'hardhat';  // Import the Hardhat runtime environment
// import { writeFileSync } from "fs";
//
//
// const CONTRACT_NAME = "NFTAuction";
//
// async function main(): Promise<void> {
//     const [deployer] = await hre.ethers.getSigners();
//     console.log("Deploying contracts with the account:", deployer.address);
//
//     const NFTAuction =
//
//     console.log(CONTRACT_NAME, "deployed to:", nftAuction.address);
//
//     const contractInfo = {
//         address: nftAuction.address,
//         abi: JSON.parse(nftAuction.interface.format("json"))
//     };
//     writeFileSync(`./artifacts/${CONTRACT_NAME}.json`, JSON.stringify(contractInfo, null, 4));
// }
//
// main()
//     .then(() => process.exit(0))
//     .catch((error) => {
//         console.error(error);
//         process.exit(1);
//     });

import {writeFileSync} from "node:fs";
import {ethers} from "hardhat";
import {deployUpgradableContract, verifyContract} from "./helpers"
import {
    NFTAuction__factory,
    NFTAuctionGovernor__factory,
    NFTAuctionToken__factory,
    NFTAuctionTimelockController__factory
} from "../typechain-types";
import {HardhatEthersSigner} from "@nomicfoundation/hardhat-ethers/signers";
import {sign} from "node:crypto";


async function deployNFTAuction(signer: HardhatEthersSigner) {
    return await deployUpgradableContract(new NFTAuction__factory(signer));
}

async function deployNFTAuctionGovernor(
    signer: HardhatEthersSigner,
    tokenAddress: string,
    timelockAddress: string,
) {
    return await deployUpgradableContract(
        new NFTAuctionGovernor__factory(signer),
        [tokenAddress, timelockAddress, signer.address]
    );
}


async function deployNFTAuctionToken(signer: HardhatEthersSigner) {
    return await deployUpgradableContract(new NFTAuctionToken__factory(signer), [signer.address]);
}

async function deployTimelock(signer: HardhatEthersSigner) {
    return await deployUpgradableContract(
        new NFTAuctionTimelockController__factory(signer),
        [50400, signer.address, [signer.address], [signer.address]]
    );
}


async function main() {
    const [deployer] = await ethers.getSigners();
    console.log("Deploying contracts with the account:", deployer.address);
    const contractAddresses = [];

    // Deploying the timelock contract
    const timelock = await deployTimelock(deployer);
    console.log("Timelock deployed to:", await timelock.getAddress());
    // Deploying the token contract
    const token = await deployNFTAuctionToken(deployer);
    console.log("Token deployed to:", await token.getAddress());
    // Deploying the governor contract
    const governor = await deployNFTAuctionGovernor(deployer, await token.getAddress(), await timelock.getAddress());
    console.log("Governor deployed to:", await governor.getAddress());

    // Deploying the auction contract
    const auction = await deployNFTAuction(deployer);
    console.log("Auction deployed to:", await auction.getAddress());

    console.log("------------------ Deployment complete ------------------")
    console.log("Waiting 30 seconds for Etherscan to index contracts")
    await sleep(30000);
    await verifyContract(await timelock.getAddress(), "contracts/dao/nfttimelockcontroller.sol:NFTAuctionTimelockController");
    await verifyContract(await governor.getAddress(), "contracts/dao/nftauctiongoverner.sol:NFTAuctionGovernor");
    await verifyContract(await auction.getAddress(), "contracts/auctions/contract.sol:NFTAuction");

    try {
        await verifyContract(await token.getAddress(), "contracts/dao/nftauctiontoken.sol:NFTAuctionToken");
    } catch (e) {
        console.log(e)
    }

    console.log("------------------ Verification complete ------------------")
    console.log("Saving address to contract_address.txt")
    // save them in the format name - address
    const detailsString = JSON.stringify(
        [
            {name: "NFTAuction", address: await auction.getAddress()},
            {name: "NFTAuctionGovernor", address: await governor.getAddress()},
            {name: "NFTAuctionToken", address: await token.getAddress()},
            {name: "NFTAuctionTimelockController", address: await timelock.getAddress()},
        ]
        , null, 4);
    writeFileSync("./contract_address.txt", detailsString);
}

function sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });