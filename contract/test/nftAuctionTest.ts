import {BigNumberish, Contract, ContractFactory, ContractTransactionResponse, Signer} from "ethers";
import {expect} from "chai";
import {ethers} from "hardhat";
import {NFTAuction, TestToken} from "../typechain-types";
import { time } from "@nomicfoundation/hardhat-toolbox/network-helpers";


describe("NFTAuction Contract", function () {

    let testToken: TestToken;
    let nftAuction: NFTAuction;

    let owner: Signer;
    let bidders: Signer[];
    beforeEach(async function () {
        [owner, ...bidders] = (await ethers.getSigners());
        console.log(bidders.length)
        nftAuction = await deployNFTAuctionContract();
        testToken = await deployTestTokenContract(await owner.getAddress());
    });

    it("should create an auction", async function () {
        console.log(await nftAuction.auctionCount())

        await createAuction();
        // Place bids
        console.log(nftAuction.auctionCount)
        for (let i = 0; i < bidders.length; i++) {
            await placeBid(1, i + 1, bidders[i]);
            if (i > 0) {
                expect(await nftAuction.pendingReturns(
                    await bidders[i - 1].getAddress())).to.equal(ethers.parseEther((i).toString())
                );
            }
        }

        await time.increase(86400);

        // End auction
        await nftAuction.connect(owner).endAuction(1);
        // Check owner of the nft
        const ownerOfNFT = await testToken.ownerOf(1);
        expect(ownerOfNFT).to.equal(await bidders[bidders.length - 1].getAddress());
        await nftAuction.connect(owner).withdraw(3);
    });

    it("Should cancel auction", async function () {
            await createAuction();
            await nftAuction.connect(owner).cancelAuction(1);
    });

    async function placeBid(
        auctionId: number,
        bidAmount: number,
        bidder: Signer
    ) {
        const bidPlaced = await nftAuction
            .connect(bidder)
            .placeBid(auctionId, {value: ethers.parseEther(bidAmount.toString()), gasLimit: 5000000});
        const auction = await nftAuction.auctions(auctionId);
        expect(auction.highestBid.amount).to.equal(ethers.parseEther(bidAmount.toString()));
    }


    async function deployNFTAuctionContract(): Promise<NFTAuction> {
        const nftAuction = await ethers.deployContract("NFTAuction");
        await nftAuction.waitForDeployment();

        await nftAuction.initialize();

        return nftAuction;
    }

    async function mintToken(to: string, tokenId: number) {
        await testToken.safeMint(to, tokenId);
        await testToken.setApprovalForAll(nftAuction.getAddress(), true);
    }

    async function deployTestTokenContract(initialOwner: string): Promise<TestToken> {
        const nft = await ethers.deployContract("TestToken", [initialOwner])
        await nft.waitForDeployment()
        return nft;
    }

    async function createAuction() {
        await mintToken(await owner.getAddress(), 1);
        const title = "Example Auction";
        const description = "This is a test auction";
        const assetAddress = testToken.getAddress();
        const assetRecordId = "1";
        const startingPrice = ethers.parseEther("1");
        const reservePrice = ethers.parseEther("1");
        const minimumIncrement = ethers.parseEther("0.1");
        const duration = 86400; // 1 day in seconds

        await nftAuction.connect(owner).createAuction(
            title,
            description,
            assetAddress,
            assetRecordId,
            startingPrice,
            reservePrice,
            minimumIncrement,
            duration,
        );
    }
});
