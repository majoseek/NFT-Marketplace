import {NFTAuction, NFTAuction__factory, OwnableUpgradeable__factory, TestToken} from "../typechain-types";
import {ethers} from "hardhat";
import {deployContract, upgradeTo, verifyContract} from "./helpers";
function sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function main() {
    const proxyAddress = "0x4315b1EBD17C3FaB983Fb01e56b6b9cBcdF435C7"
    const [owner] = await ethers.getSigners();
    const contractFactory = new NFTAuction__factory(owner);
    const contract = await upgradeTo(proxyAddress, contractFactory);

    console.log("Contract deployed to:", await contract.getAddress());

    // wait for the next block
    await sleep(30000)
    // await verifyContract(await contract.getAddress());
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });