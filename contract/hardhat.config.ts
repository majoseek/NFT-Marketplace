import "@nomiclabs/hardhat-waffle";
import "@nomiclabs/hardhat-etherscan";
import { HardhatUserConfig } from "hardhat/types";

const config: HardhatUserConfig = {
  solidity: "0.8.20",
  defaultNetwork: "sepolia",
  networks: {
    hardhat: {},
    // Define your networks here. E.g., Rinkeby:
    sepolia: {
      url: "https://eth-sepolia.g.alchemy.com/v2/ovyPoWKJlQmcP-HyAIxW9lSD1X17ohVG",
      chainId: 11155111,
      accounts: ["c69359e5edd950f2d638b6e5cd902255fc8a15f371a928ac7188638302e912b5"]
    },
  },
  // @ts-ignore
  etherscan: {
    apiKey: {
      sepolia: "NTV3KPF5MBYXG8HDQVTF31DG4HH5N3EDMI"
    },
  }
};

export default config;
