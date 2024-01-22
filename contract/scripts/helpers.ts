import {ethers, run, upgrades} from "hardhat";
import {type BaseContract, Contract, ContractFactory, Signer} from "ethers";
import '@openzeppelin/hardhat-upgrades';

export async function deployUpgradableContract(
    contractFactory: ContractFactory,
    args: any[] = [],
): Promise<BaseContract> {

    const contract = await upgrades.deployProxy(
        contractFactory,
        args,
        {
            initializer: "initialize",
            kind: "uups",
            verifySourceCode: true,
        },

    )
    await contract.waitForDeployment();
    return contract;
}

export async function upgradeTo(
    proxyAddress: string,
    contractFactory: ContractFactory,
): Promise<Contract> {
    return await upgrades.upgradeProxy(
        proxyAddress,
        contractFactory,
    );
}

export async function verifyContract(
    contractAddress: string,
    name: string,
    retries: number = 5
): Promise<void> {
    try {
        await run("verify:verify", {address: contractAddress, network: "sepolia", contract: name});
    } catch (e) {
        console.log(`Failed to verify contract ${contractAddress}`)
        console.log(e)
        if (retries > 0) {
            console.log(`Retrying in 5 seconds...`)
            await new Promise((resolve) => setTimeout(resolve, 40000))
            return await verifyContract(contractAddress, name, retries - 1)
        }
        throw e
    }
}

export async function deployContract(
    name: string,
    signer: Signer
): Promise<BaseContract> {
    const contract = await ethers.deployContract(name, {
        signer: signer,
    })
    await contract.waitForDeployment();
    return contract;
}