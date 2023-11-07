
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "./status.sol";

struct Bid {
    address bidder;
    uint256 amount;
}

struct Auction {
    uint256 auctionRecordId;
    string title;
    string description;
    address assetAddress;
    uint256 assetRecordId;
    uint128 startingPrice;
    uint128 reservePrice;
    uint128 minimumIncrement;
    uint256 expiryTime;
    Status status;
    address sellerAddress;
    Bid highestBid;
}
