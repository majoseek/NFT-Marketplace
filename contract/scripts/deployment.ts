import { HardhatRuntimeEnvironment } from "hardhat/types";
import { Contract, ContractFactory } from "ethers";
import hre from 'hardhat';  // Import the Hardhat runtime environment
import { writeFileSync } from "fs";


const AUCTION_CONTRACT_NAME = "NFTAuction";
const GOVERNANCE_CONTRACT_NAME = "NFTAuctionGovernor";
const TOKEN_CONTRACT_NAME = "NFTAuctionToken";

interface DeployedContract {
    name: string;
    address: string;
}

async function main(): Promise<void> {
    await hre.run("compile");

    console.log(`Deploying contracts to ${hre.network.name}...`);
    const contractDetails: Array<DeployedContract> = [];

    const contracts = [
        AUCTION_CONTRACT_NAME,
        GOVERNANCE_CONTRACT_NAME,
        TOKEN_CONTRACT_NAME
    ];
    for (const contractName of contracts) {
        const deployed = await deployContract(hre, contractName);
        if (hre.network.name == "sepolia") {
            await verifyContract(hre, contractName, deployed.address);
        }
        contractDetails.push({ name: contractName, address: deployed.address });
    };

    writeFileSync("./contract_details.json", JSON.stringify(contractDetails));
}


async function deployContract(
    hre: HardhatRuntimeEnvironment,
    contractName: string,
): Promise<Contract> {
    console.log(`Deploying ${contractName}...`);
    const contract = await hre.ethers.getContractFactory(contractName);
    const deployed = await contract.deploy();
    await deployed.deployed();
    await deployed.deployTransaction.wait(5); // waits for 5 confirmations
    console.log("Contract transaction hash: ", deployed.deployTransaction.hash);
    console.log(`${contractName} deployed to: ${deployed.address}`);
    return deployed;
}

async function verifyContract(
    hre: HardhatRuntimeEnvironment,
    contractName: string,
    contractAddress: string,
) {
    console.log(`Verifying ${contractName} at: ${contractAddress}`);
    await hre.run("verify:verify", {
        address: contractAddress,
        constructorArguments: [],
    });
    console.log(`${contractName} verified at: ${contractAddress}`);
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });
