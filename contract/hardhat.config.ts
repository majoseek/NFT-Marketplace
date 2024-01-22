import "@nomicfoundation/hardhat-toolbox";
import "@nomicfoundation/hardhat-chai-matchers"
import "hardhat-gas-reporter"

import { HardhatUserConfig } from "hardhat/types";
import '@openzeppelin/hardhat-upgrades';

const config: HardhatUserConfig = {
  solidity: {
    version: "0.8.22",
    settings: {
      viaIR: true,
      optimizer: {
        enabled: true,
        runs: 1000,
      },
    },
  },
  defaultNetwork: "sepolia",
  networks: {
    hardhat: {},
    // Define your networks here. E.g., Rinkeby:
    sepolia: {
      url: "https://eth-sepolia.g.alchemy.com/v2/ovyPoWKJlQmcP-HyAIxW9lSD1X17ohVG",
      chainId: 11155111,
      // accounts: ["c68f8a72480cded7ef8e03ccb856ed717afc76591eac169c68b3f2ba3363a410"],
      accounts: ["c69359e5edd950f2d638b6e5cd902255fc8a15f371a928ac7188638302e912b5", "6d86256ad0593d98b39c70033338635b4801dfda2675f2bbd100e796373a50d9"]
    },
    localhost: {
        url: "http://127.0.0.1:8545"
    }
  },
  gasReporter: {
    enabled: true,
    token: "ETH",
    currency: "USD",
    coinmarketcap: "8e1f6ab3-7ef1-4139-87a1-bbafa0184260",
    gasPriceApi: "https://api.etherscan.io/api?module=proxy&action=eth_gasPrice",
  },
  // @ts-ignore
  etherscan: {
    apiKey: {
      sepolia: "NTV3KPF5MBYXG8HDQVTF31DG4HH5N3EDMI"
    },
  }
};

export default config;
// task("upgrade", "Upgrades NFTAuctionContract")
//     .addParam("oldContractAddress", "The address of the old contract")
//     .setAction(async (taskArgs, hre) => {
//         const [deployer] = await ethers.getSigners();
//         console.log("Deploying contracts with the account:", deployer.address);
//         const newImplementation = await deployContract("NFTAuction", deployer) as NFTAuction;
//
//         const oldContractAddress = process.argv[2];
//         // Check if it is a valid address
//         if (!ethers.isAddress(oldContractAddress)) {
//             throw new Error("Invalid address");
//         }
//
//         await upgradeTo(oldContractAddress, await newImplementation.getAddress());
//     })