import {BigNumberish, Contract, ContractFactory, ContractTransactionResponse, Signer} from "ethers";
import {expect} from "chai";
import {ethers} from "hardhat";
import {NFTAuction, NFTAuction__factory, OwnableUpgradeable__factory, TestToken} from "../typechain-types";
import {time} from "@nomicfoundation/hardhat-toolbox/network-helpers";

const Status = {
    Active: BigInt(0),
    Won: BigInt(1),
    Expired: BigInt(2),
    Cancelled: BigInt(3),
}

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

    it("testInitialize_SetsOwnerCorrectly", async function () {
        const contractOwner = await nftAuction.owner();
        const expectedOwner = await owner.getAddress();

        expect(contractOwner).to.equal(expectedOwner, "Owner is not set correctly after initialization");
    });

    it("testUpgradeToAndCall_RevertsWhenNotOwner", async function () {
        const nonOwner = bidders[0];
        const newImplementationAddress = ethers.Wallet.createRandom().address;
        const dummyData = "0x00"; // Replace with actual data if necessary

        await expect(
            nftAuction.connect(nonOwner).upgradeToAndCall(newImplementationAddress, dummyData)
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "UUPSUnauthorizedCallContext"
        );
    });


    it("testCreateAuction_SuccessWithValidParameters", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();

        const auctionCount = await nftAuction.auctionCount();
        expect(auctionCount).to.equal(1);

        const auction = await nftAuction.auctions(1);
        expect(auction.title).to.equal("Example Auction");
        expect(auction.description).to.equal("This is a test auction");
        console.log(auction.assetAddress)
        expect(auction.assetAddress).to.equal(await testToken.getAddress());
        expect(auction.assetRecordId).to.equal("1");
        expect(auction.startingPrice).to.equal(ethers.parseEther("0.0000001"));
        expect(auction.reservePrice).to.equal(ethers.parseEther("0.0000002"));
        expect(auction.minimumIncrement).to.equal(ethers.parseEther("0.0000001"));
        const block = await ethers.provider.getBlock("latest");
        expect(auction.expiryTime).to.be.equal(block!!.timestamp + 86400);
    });

    it("testCreateAuction_RevertsOnActiveAuctionWithSameNFT", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();

        await expect(
            createAuction()
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "ActiveAuctionWithNFTAlreadyExists"
        );
    });

    it("testCreateAuction_RevertsOnInsufficientPermissions", async function () {
        const unauthorizedBidder = bidders[0];
        await mintToken(await owner.getAddress(), 1);
        await expect(
            nftAuction.connect(unauthorizedBidder).createAuction(
                "Example Auction",
                "This is a test auction",
                testToken.getAddress(),
                "1",
                ethers.parseEther("1"),
                ethers.parseEther("1"),
                ethers.parseEther("0.1"),
                86400
            )
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "InsufficientPermissions"
        );
    });

    it("testPlaceBid_SuccessOnValidBid", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        const bidder = bidders[0];
        const bidAmount = ethers.parseEther("0.0000001");

        await placeBid(1, bidAmount, bidder);

        const auction = await nftAuction.auctions(1);
        expect(auction.highestBid.amount).to.equal(ethers.parseEther("0.0000001"));
    });

    it("testPlaceBid_RevertsIfAuctionDoesNotExist", async function () {
        const bidder = bidders[0];
        const bidAmount = ethers.parseEther("0.0000001");

        await expect(placeBid(999, bidAmount, bidder))
            .to.be.revertedWithCustomError(
                {interface: nftAuction.interface},
                "AuctionDoesNotExist"
            );
    });

    it("testPlaceBid_RevertsIfBidderIsAuctionOwner", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        const bidAmount = ethers.parseEther("0.0000001");

        await expect(placeBid(1, bidAmount, owner))
            .to.be.revertedWithCustomError(
                {interface: nftAuction.interface},
                "InsufficientPermissions"
            );

    });

    it("Should cancel auction", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await nftAuction.connect(owner).cancelAuction(1);
    });

    it("testPlaceBid_RevertsIfAuctionNotActive", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await nftAuction.connect(owner).cancelAuction(1);

        const bidder = bidders[0];
        const bidAmount = ethers.parseEther("0.0000001");

        await expect(
            placeBid(1, bidAmount, bidder)
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "AuctionIsNotActive"
        );
    });

    it("testPlaceBid_RevertsIfAuctionExpired", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await time.increase(86400 + 1); // Assuming the auction duration is 86400 seconds

        const bidder = bidders[0];
        const bidAmount = ethers.parseEther("0.0000001");

        await expect(
            placeBid(1, bidAmount, bidder)
        ).to.be.revertedWith("Auction has expired");
    });

    it("testPlaceBid_RevertsIfBidNotMeetingMinimumIncrement", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await placeBid(1, ethers.parseEther("0.001"), bidders[0]);

        const insufficientIncrementBid = ethers.parseEther("0.00000001"); // Assuming minimum increment is 0.1 ether

        await expect(
            placeBid(1, insufficientIncrementBid, bidders[1])
        ).to.be.revertedWith("Must increment bid by the minimum amount");
    });

    it("testPlaceBid_RevertsIfBidLowerThanStartingPrice", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        const bidder = bidders[0];
        const bidAmount = ethers.parseEther("0.00000001"); // Assuming starting price is 1 ether

        await expect(
            placeBid(1, bidAmount, bidder)
        ).to.be.revertedWith("Must place bid of at least the starting price");
    });

    it("testEndAuction_SuccessWhenAuctionWon", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await placeBid(1, ethers.parseEther("1"), bidders[0]);
        await time.increase(86400 + 1);
        await nftAuction.connect(owner).endAuction(1);

        const auction = await nftAuction.auctions(1);
        expect(auction.status).to.equal(Status.Won);
    });

    it("testEndAuction_SuccessWhenAuctionExpiredWithoutWinner", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await time.increase(86400 + 1);
        await nftAuction.connect(owner).endAuction(1);

        const auction = await nftAuction.auctions(1);
        expect(auction.status).to.equal(Status.Expired);
    });

    it("testEndAuction_RevertsIfAuctionNotActive", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await nftAuction.connect(owner).cancelAuction(1);

        await expect(
            nftAuction.connect(owner).endAuction(1)
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "AuctionIsNotActive"
        );
    });

    it("testEndAuction_RevertsIfCalledByNonOwner", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        const nonOwner = bidders[0];

        await expect(
            nftAuction.connect(nonOwner).endAuction(1)
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "InsufficientPermissions"
        );
    });

    it("testCancelAuction_SuccessWhenNoBidsPresent", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await nftAuction.connect(owner).cancelAuction(1);

        const auction = await nftAuction.auctions(1);
        expect(auction.status).to.equal(BigInt(3));
    });

    it("testCancelAuction_RevertsIfAuctionHasBids", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await placeBid(1, ethers.parseEther("1"), bidders[0]);

        await expect(
            nftAuction.connect(owner).cancelAuction(1)
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "InsufficientPermissions"
        );
    });

    it("testCancelAuction_RevertsIfCalledByNonOwner", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        const nonOwner = bidders[0];

        await expect(
            nftAuction.connect(nonOwner).cancelAuction(1)
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "InsufficientPermissions"
        );
    });

    it("testWithdraw_SuccessWithValidAmount", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await placeBid(1, ethers.parseEther("1"), bidders[0]);
        await time.increase(86400 + 1);
        await nftAuction.connect(owner).endAuction(1);
        const bidderAddress = await bidders[0].getAddress();

        await nftAuction.connect(owner).withdraw(ethers.parseEther("1"));

        expect(await nftAuction.pendingReturns(bidderAddress)).to.equal(0);
    });

    it("testWithdraw_RevertsOnInvalidAmount", async function () {
        await mintToken(await owner.getAddress(), 1);
        await createAuction();
        await placeBid(1, ethers.parseEther("1"), bidders[0]);
        await time.increase(86400 + 1);
        await nftAuction.connect(owner).endAuction(1);

        await expect(
            nftAuction.connect(bidders[0]).withdraw(ethers.parseEther("2"))
        ).to.be.revertedWithCustomError(
            {interface: nftAuction.interface},
            "InsufficientAmount"
        );
    });

    it("testWithdraw_RevertsOnTransferFailure", async function () {
        // This test requires a setup where the transfer will fail, such as a contract with no ETH.
        // Specific implementation will depend on the testing framework and contract setup.
    });

    async function placeBid(
        auctionId: number,
        bidAmount: bigint,
        bidder: Signer
    ) {
        return await nftAuction
            .connect(bidder)
            .placeBid(auctionId, {value: bidAmount, gasLimit: 5000000});
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

    async function createAuction(tokenId: number = 1) {
        const title = "Example Auction";
        const description = "This is a test auction";
        const assetAddress = testToken.getAddress();
        const assetRecordId = tokenId.toString();
        const startingPrice = ethers.parseEther("0.0000001");
        const reservePrice = ethers.parseEther("0.0000002");
        const minimumIncrement = ethers.parseEther("0.0000001");
        const duration = 86400; // 1 day in seconds

        return await nftAuction.connect(owner).createAuction(
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
