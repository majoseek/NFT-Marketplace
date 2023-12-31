// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "./status.sol";

    struct Bid {
        uint96 amount;
        address bidder;
    }

    struct Auction {
        uint256 auctionRecordId;
        uint256 assetRecordId;
        uint256 expiryTime;
        Status status;
        uint256 startingPrice;
        uint256 reservePrice;
        uint256 minimumIncrement;
        address assetAddress;
        address sellerAddress;
        Bid highestBid;
        string title;
        string description;
    }
