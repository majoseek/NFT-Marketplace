import {NFTAuction, NFTAuction__factory, OwnableUpgradeable__factory, TestToken} from "../typechain-types";
import {ethers} from "hardhat";
import {deployContract} from "../scripts/helpers";
import {BaseContract} from "ethers";
import {task} from "hardhat/config";


async function upgradeTo(
    initialAddress: string,
    newImplementationAddress: string
) {
    const nftAuction = await ethers.getContractAt("NFTAuction", initialAddress);
    const initializeData = nftAuction.interface.encodeFunctionData(
        "initialize",
    );
    const tx = await nftAuction.upgradeToAndCall(
        newImplementationAddress,
        initializeData
    );
    const response = await tx.wait()

    console.log("Contract upgraded", newImplementationAddress);
    console.log("Transaction hash:", response?.hash);
    console.log("Gas used:", response?.cumulativeGasUsed.toString());
}



