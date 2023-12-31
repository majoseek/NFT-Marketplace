// import { ethers } from "ethers";
//
// // Your Ethereum wallet private key (keep it secret)
// const privateKey = process.env.CREATE_AUCTION_WALLET; // Replace with your private key
//
// // The address of the deployed contract
// const contractAddress = "0xE49E3638Ef0411F10B311451D277904C8C69eebd"; // Replace with your contract's address
//
// // The ABI (Application Binary Interface) of your contract
// const contractABI = [
//   // Include only the relevant function ABI for mintNFT
//   "function mintNFT(string memory _tokenURI) public"
//   // ... other functions as needed
// ];
//
// // Ethereum provider URL (e.g., Infura, Alchemy, or your local node)
//
// if (!privateKey) {
//     throw new Error("Please set your private key in your environment.");
// }
//
// const provider = new ethers.providers.JsonRpcProvider(process.env.SEPOLIA_API_URL);
// const wallet = new ethers.Wallet(privateKey, provider);
// const contract = new ethers.Contract(contractAddress, contractABI, wallet);
//
// async function mintNFT(tokenURI: string) {
//   // Call the mintNFT function from the smart contract
//   const tx = await contract.mintNFT(tokenURI, { gasLimit: 500000 });
//   console.log("Transaction hash:", tx.hash);
//
//   // Wait for the transaction to be confirmed
//   const receipt = await tx.wait();
//   console.log("Transaction confirmed in block:", receipt.blockNumber);
// }
//
// async function main(): Promise<void> {
//     for (let i = 1; i < 5; i++) {
//         const tokenURI = "https://altlayer-image-store.alt.technology/msnft/metadata";
//         await mintNFT(tokenURI);
//     }
// }
//
// main()
//     .then(() => process.exit(0))
//     .catch((error) => {
//         console.error(error);
//         process.exit(1);
//     });
