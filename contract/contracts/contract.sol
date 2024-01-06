// SPDX-License-Identifier: MIT
pragma solidity ^0.8.22;
import {Auction, Status, Bid} from "./data/auction.sol";
import {IERC721} from "@openzeppelin/contracts/token/ERC721/IERC721.sol";
import {UUPSUpgradeable} from "@openzeppelin/contracts/proxy/utils/UUPSUpgradeable.sol";
import {OwnableUpgradeable} from "@openzeppelin/contracts-upgradeable/access/OwnableUpgradeable.sol";
import {
    InsufficientAmount,
    InsufficientPermissions,
    AuctionDoesNotExist,
    ActiveAuctionWithNFTAlreadyExists,
    AuctionIsNotActive
} from "./errors.sol";

contract NFTAuction is UUPSUpgradeable, OwnableUpgradeable {
    uint32 public auctionCount = 0;

    mapping(uint32 => Auction) public auctions;
    mapping(address => uint96) public pendingReturns;
    mapping(address => mapping(uint32 => uint32)) public activeAuctionsByNFT;


    event AuctionCreated(uint32 auctionId);
    event AuctionCanceled(uint32 auctionId);
    event BidPlaced(uint32 auctionId, address bidder, uint96 amount);
    event AuctionEndedWithWinner(uint32 auctionId);
    event AuctionEndedWithoutWinner(uint32 auctionId);

    modifier auctionExists(uint32 auctionId) {
        if (auctionId > auctionCount) {
            revert AuctionDoesNotExist(auctionId);
        }
        _;
    }

    modifier onlyAuctionOwner(uint32 auctionId) {
        if (!isAuctionOwner(msg.sender, auctionId)) {
            revert InsufficientPermissions();
        }
        _;
    }

    function initialize() public initializer {
        __Ownable_init(msg.sender);
    }

    function _authorizeUpgrade(address newImplementation) internal override onlyOwner {}

    function createAuction(
        string calldata title,
        string calldata description,
        address assetAddress,
        uint32 assetRecordId,
        uint96 startingPrice,
        uint96 reservePrice,
        uint96 minimumIncrement,
        uint32 duration
    ) external {
        if (activeAuctionsByNFT[assetAddress][assetRecordId] > 0) {
            revert ActiveAuctionWithNFTAlreadyExists(assetAddress, assetRecordId);
        }

        IERC721 token = IERC721(assetAddress);
        if (token.getApproved(assetRecordId) != address(this) && !token.isApprovedForAll(msg.sender, address(this))) {
            revert InsufficientPermissions();
        }

        uint32 newAuctionId = ++auctionCount;
        Auction storage newAuction = auctions[newAuctionId];

        newAuction.auctionRecordId = newAuctionId;
        newAuction.title = title;
        newAuction.description = description;
        newAuction.assetAddress = assetAddress;
        newAuction.assetRecordId = assetRecordId;
        newAuction.startingPrice = startingPrice;
        newAuction.reservePrice = reservePrice;
        newAuction.minimumIncrement = minimumIncrement;
        newAuction.expiryTime = uint32(block.timestamp + duration);
        newAuction.status = Status.Active;
        newAuction.sellerAddress = msg.sender;
        activeAuctionsByNFT[assetAddress][assetRecordId] = newAuctionId;

        token.transferFrom(msg.sender, address(this), newAuction.assetRecordId);

        emit AuctionCreated(newAuction.auctionRecordId);
    }

    function placeBid(uint32 auctionId) external payable auctionExists(auctionId) {
        if (isAuctionOwner(msg.sender, auctionId)) {
            revert InsufficientPermissions();
        }
        if (msg.value > type(uint96).max) {
            revert InsufficientAmount(type(uint96).max);
        }

        Auction storage auction = auctions[auctionId];

        if (auction.status != Status.Active) {
            revert AuctionIsNotActive(auctionId);
        }


        require(auction.expiryTime > block.timestamp, "Auction has expired");

        if (hasBid(auction)) {
            require(msg.value >= auction.highestBid.amount + auction.minimumIncrement, "Must increment bid by the minimum amount");
            pendingReturns[auction.highestBid.bidder] += auction.highestBid.amount;
        } else {
            require(msg.value >= auction.startingPrice, "Must place bid of at least the starting price");
        }

        auction.highestBid = Bid(
            {
                bidder: msg.sender,
                amount: uint96(msg.value)
            }
        );
        emit BidPlaced(auctionId, msg.sender, auction.highestBid.amount);
    }

    function endAuction(uint32 auctionId) external onlyAuctionOwner(auctionId) {
        Auction storage auction = auctions[auctionId];
        if(auction.status != Status.Active){
            revert AuctionIsNotActive(auctionId);
        }

        if (hasBid(auction)) {
            Bid storage highestBid = auction.highestBid;

            if (highestBid.amount >= auction.reservePrice) {
                pendingReturns[auction.sellerAddress] += highestBid.amount;
                auction.status = Status.Won;

                IERC721 token = IERC721(auction.assetAddress);
                token.transferFrom(address(this), highestBid.bidder, auction.assetRecordId);
                emit AuctionEndedWithWinner(auctionId);
            } else {
                pendingReturns[highestBid.bidder] += highestBid.amount;
                auction.status = Status.Expired;
                emit AuctionEndedWithoutWinner(auctionId);
            }
        } else {
            auction.status = Status.Expired;
            emit AuctionEndedWithoutWinner(auctionId);
        }
        activeAuctionsByNFT[auction.assetAddress][auction.assetRecordId] = 0;
    }

    function cancelAuction(uint32 auctionId) external onlyAuctionOwner(auctionId) {
        Auction storage auction = auctions[auctionId];

        if (hasBid(auction)) {
            revert InsufficientPermissions();
        }

        IERC721 token = IERC721(auction.assetAddress);
        token.transferFrom(address(this), msg.sender, auction.assetRecordId);

        auction.status = Status.Canceled;
        emit AuctionCanceled(auctionId);
    }

    function withdraw(uint96 amount) external {
        uint96 maximumReturn = pendingReturns[msg.sender];
        if (amount <= 0 || amount > maximumReturn) {
            revert InsufficientAmount(maximumReturn);
        }

        unchecked {
            pendingReturns[msg.sender] -= amount;
        }
        (bool success,) = msg.sender.call{value: amount}("");
        require(success, "Transfer failed.");
    }

    function isAuctionOwner(address _user, uint32 _auctionId) private view returns (bool) {
        return auctions[_auctionId].sellerAddress == _user;
    }

    function hasBid(Auction storage auction) private view returns (bool) {
        return auction.highestBid.bidder != address(0);
    }
}
