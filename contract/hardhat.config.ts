import "@nomicfoundation/hardhat-toolbox";
import "@nomicfoundation/hardhat-chai-matchers"
import "hardhat-gas-reporter"

import { HardhatUserConfig } from "hardhat/types";

const config: HardhatUserConfig = {
  solidity: {
    version: "0.8.20",
    settings: {
      viaIR: true,
      optimizer: {
        enabled: true,
        runs: 600,
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
      accounts: ["c69359e5edd950f2d638b6e5cd902255fc8a15f371a928ac7188638302e912b5"]
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
