### Contract overview - [0x71C822292561C116232d350df0d8caCF0D32Af20](https://sepolia.etherscan.io/address/0x71C822292561C116232d350df0d8caCF0D32Af20#code)

**Read functions:**

1. `getAuctionStatus`: 
    - Parameters: `auctionId` (Unique identifier of the auction)
    - Returns: The status of the auction (`Pending`, `Active`, or `Inactive`).
    - Functionality: It fetches the current status of the auction specified by the `auctionId`.

2. `getAuctionsByUser`: 
    - Parameters: `user` (Address of the user)
    - Returns: An array of auction ids that were created by the specified user.
    - Functionality: It fetches all the auctions created by a specific user.

**Write functions:**

1. `createAuction`: 
    - Parameters: title, description, asset address(nft contract address), asset record id(nft token id), starting price, reserve price(minimum price which seller would be willing to accept from a buyer), minimum increment, duration (in seconds), distribution cut (percentage of final price our marketplace gets)
    - Returns: Emits an `AuctionCreated` event, but no return value.
    - Functionality: It creates a new auction with the specified parameters and makes it active.
    - Signing required: Yes

2. `placeBid`: 
    - Parameters: `auctionId` (Unique identifier of the auction)
    - Returns: Emits a `BidPlaced` event, but no return value.
    - Functionality: It places a bid on an active auction.
    - Signing required: Yes

3. `endAuction`: 
    - Parameters: `auctionId` (Unique identifier of the auction)
    - Returns: Emits either an `AuctionEndedWithWinner` or an `AuctionEndedWithoutWinner` event, but no return value.
    - Functionality: It ends an auction, transferring the NFT to the highest bidder if the reserve price was met.
    - Signing required: Yes

4. `cancelAuction`: 
    - Parameters: `auctionId` (Unique identifier of the auction)
    - Returns: Emits an `AuctionCancelled` event, but no return value.
    - Functionality: It cancels an auction and refunds the last bid, if any.
    - Signing required: Yes

5. `extendAuctionTime`: 
    - Parameters: `auctionId` (Unique identifier of the auction), `additionalTime` (The amount of time to extend the auction by, in seconds)
    - Returns: Emits an `AuctionExtended` event, but no return value.
    - Functionality: It extends the duration of an auction by the specified amount of time.
    - Signing required: Yes

6. `withdraw`: 
    - Parameters: None
    - Returns: Emits an `AuctionWithdrawn` event, but no return value.
    - Functionality: It allows a user to withdraw any pending returns they have accrued from placing bids.
    - Signing required: Yes


**Public functions:**

1. `auctions`: 
    - Parameters: `auctionId` (Unique identifier of the auction)
    - Returns: An `Auction` object with all its fields (auctionRecordId, title, description, assetAddress, assetRecordId, startingPrice, reservePrice, minimumIncrement, distributionCut, expiryTime, status, sellerAddress, highestBid).
    - Functionality: It fetches all the data related to an auction specified by the `auctionId`.

**Events:**

1. `AuctionCreated`: 
    - Parameters: `id` (The ID of the created auction), `title` (Title of the auction), `startingPrice` (Starting price of the auction), `reservePrice` (Reserve price of the auction), `expiryTime` (expiry time of the auction)
    - Functionality: Emitted when a new auction is created.

2. `AuctionCancelled`: 
    - Parameters: `id` (The ID of the cancelled auction)
    - Functionality: Emitted when an auction is cancelled by its owner.

3. `BidPlaced`: 
    - Parameters: `auctionId` (The ID of the auction), `bidder` (Address of the bidder), `amount` (Amount of the bid)
    - Functionality: Emitted when a new bid is placed in an auction.

4. `AuctionEndedWithWinner`: 
    - Parameters: `auctionId` (The ID of the auction), `winningBidder` (Address of the winning bidder), `amount` (Amount of the winning bid)
    - Functionality: Emitted when an auction ends successfully with a winner.

5. `AuctionEndedWithoutWinner`: 
    - Parameters: `auctionId` (The ID of the auction), `topBid` (Highest bid amount), `reservePrice` (Reserve price of the auction)
    - Functionality: Emitted when an auction ends without any winner i.e., no bid met the reserve price.

6. `AuctionExtended`: 
    - Parameters: `auctionId` (The ID of the auction), `newExpiryTime` (New expiry timestamp of the auction)
    - Functionality: Emitted when the duration of an auction is extended.

7. `AuctionWithdrawn`: 
    - Parameters: `withdrawer` (Address of the withdrawer), `amount` (Amount withdrawn)
    - Functionality: Emitted when a user withdraws their pending returns.

8. `NFTTransferred`: 
    - Parameters: `from` (Address of the sender), `to` (Address of the receiver), `tokenId` (ID of the transferred NFT)
    - Functionality: Emitted when an NFT is transferred from the auction contract to a user.

9. `LogFailure`: 
    - Parameters: `message` (The failure message)
    - Functionality: Emitted when an error or failure occurs in the contract.

