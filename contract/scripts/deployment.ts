import { HardhatRuntimeEnvironment } from "hardhat/types";
import { ContractFactory } from "ethers";
import hre from 'hardhat';  // Import the Hardhat runtime environment
import { writeFileSync } from "fs";


const CONTRACT_NAME = "NFTAuction";

async function main(): Promise<void> {
    // Compile the contract
    await hre.run("compile",  { network: "sepolia" });

    // Deploy contract
    const Contract = await hre.ethers.getContractFactory(CONTRACT_NAME);
    const contract = await Contract.deploy();
    await contract.deployed();
    await contract.deployTransaction.wait(5); // waits for 5 confirmations
    
    
    console.log("Contract transaction hash: ", contract.deployTransaction.hash);
    console.log(`${CONTRACT_NAME} deployed to: ${contract.address}`);

    // Verify contract
    await hre.run("verify:verify", {
        address: contract.address,
        constructorArguments: [],  
    });

    // after deploy
    // set the contract address in the backend
    // and replace files
    // write to file contract_address.txt 
    writeFileSync('contract_address.txt', contract.address);
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });
