// SPDX-License-Identifier: MIT
pragma solidity ^0.8.22;

import "./status.sol";

    struct Bid {
        uint96 amount;
        address bidder;
    }

    struct Auction {
        uint32 auctionRecordId;
        uint32 assetRecordId;
        uint32 expiryTime;
        Status status;
        uint96 startingPrice;
        uint96 reservePrice;
        uint96 minimumIncrement;
        address assetAddress;
        address sellerAddress;
        Bid highestBid;
        string title;
        string description;
    }
