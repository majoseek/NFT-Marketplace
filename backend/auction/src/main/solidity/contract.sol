// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;
import {Auction, Status, Bid} from "./data/auction.sol";
import {IERC721} from "@openzeppelin/contracts/token/ERC721/IERC721.sol";
import {
    InsufficientAmount,
    InsufficientPermissions,
    AuctionDoesNotExist,
    ActiveAuctionWithNFTAlreadyExists,
    AuctionIsNotActive
} from "./errors.sol";

contract NFTAuction {
    address public immutable contractOwner;
    uint public auctionCount = 0;


    mapping(uint => Bid[]) public bidsByAuctionId; // TODO: reconsider this
    mapping(uint => Auction) public auctions;
    mapping(address => uint) public pendingReturns;
    mapping(address => mapping(uint => uint)) public activeAuctionsByNFT;


    event AuctionCreated(uint auctionId);
    event AuctionCanceled(uint auctionId);
    event BidPlaced(uint auctionId, address bidder, uint amount);
    event AuctionEndedWithWinner(uint auctionId, address winningBidder, uint amount);
    event AuctionEndedWithoutWinner(uint auctionId, uint reservePrice);
    event LogFailure(string message);
    event AuctionExtended(uint auctionId, uint newExpiryTime);
    event AuctionWithdrawn(address withdrawer, uint amount);
    event NFTTransferred(address from, address to, uint tokenId);


    modifier auctionExists(uint auctionId) {
        if (auctionId > auctionCount) {
            revert AuctionDoesNotExist(auctionId);
        }
        _;
    }

    modifier onlyAuctionOwner(uint auctionId) {
        if (!isAuctionOwner(msg.sender, auctionId)) {
            revert InsufficientPermissions();
        }
        _;
    }

    modifier onlyContractOwner() {
        if (msg.sender != contractOwner) {
            revert InsufficientPermissions();
        }
        _;
    }

    modifier onlyOneActiveAuctionPerNFT(address nftAddress, uint nftTokenId) {
        if (activeAuctionsByNFT[nftAddress][nftTokenId] == 0) {
            revert ActiveAuctionWithNFTAlreadyExists(nftAddress, nftTokenId);
        }
        _;
    }

    modifier onlyNFTOwner(address nftAddress, uint nftTokenId) {
        IERC721 token = IERC721(nftAddress);
        if (token.getApproved(nftTokenId) != address(this) || !token.isApprovedForAll(msg.sender, address(this))) {
            revert InsufficientPermissions();
        }
        _;
    }

    constructor() {
        contractOwner = msg.sender;
    }

    function createAuction(
        string memory title,
        string memory description,
        address assetAddress,
        uint assetRecordId,
        uint128 startingPrice,
        uint128 reservePrice,
        uint128 minimumIncrement,
        uint128 duration
    ) public onlyOneActiveAuctionPerNFT(assetAddress, assetRecordId) onlyNFTOwner(assetAddress, assetRecordId) {
        IERC721 token = IERC721(assetAddress);

        Auction storage newAuction = auctions[++auctionCount];

        newAuction.auctionRecordId = auctionCount;
        newAuction.title = title;
        newAuction.description = description;
        newAuction.assetAddress = assetAddress;
        newAuction.assetRecordId = assetRecordId;
        newAuction.startingPrice = startingPrice;
        newAuction.reservePrice = reservePrice;
        newAuction.minimumIncrement = minimumIncrement;
        newAuction.expiryTime = block.timestamp + duration;
        newAuction.status = Status.Active;
        newAuction.sellerAddress = msg.sender;
        activeAuctionsByNFT[assetAddress][assetRecordId] = auctionCount;

        token.transferFrom(msg.sender, address(this), newAuction.assetRecordId);

        emit AuctionCreated(newAuction.auctionRecordId);
    }

    function placeBid(uint auctionId) public payable auctionExists(auctionId) {
        Auction storage auction = auctions[auctionId];


        if (isAuctionOwner(msg.sender, auctionId)) {
            revert InsufficientPermissions();
        }
        if (auction.status != Status.Active) {
            revert AuctionIsNotActive(auctionId);
        }
        require(auction.expiryTime > block.timestamp, "Auction has expired"); // TODO think about error

        if (hasBid(auction)) {
            require(msg.value >= auction.highestBid.amount + auction.minimumIncrement, "Must increment bid by the minimum amount");
            pendingReturns[auction.highestBid.bidder] += auction.highestBid.amount;
        } else {
            require(msg.value >= auction.startingPrice, "Must place bid of at least the starting price");
        }

        auction.highestBid = Bid(
            {
                bidder: msg.sender,
                amount: msg.value
            }
        );

        bidsByAuctionId[auctionId].push(auction.highestBid);

        emit BidPlaced(auctionId, msg.sender, msg.value);
    }

    function endAuction(uint auctionId) public onlyAuctionOwner(auctionId) {
        Auction storage auction = auctions[auctionId];
        if(auction.status != Status.Active){
            revert AuctionIsNotActive(auctionId);
        }

        if (hasBid(auction)) {
            Bid memory highestBid = auction.highestBid;

            if (highestBid.amount >= auction.reservePrice) {
                // Think about it if we need distribution cut
                pendingReturns[auction.sellerAddress] += highestBid.amount;

                IERC721 token = IERC721(auction.assetAddress);
                token.transferFrom(address(this), highestBid.bidder, auction.assetRecordId);
                auction.status = Status.Won;
                emit NFTTransferred(address(this), highestBid.bidder, auction.assetRecordId);
                emit AuctionEndedWithWinner(auctionId, highestBid.bidder, highestBid.amount);
            } else {
                pendingReturns[highestBid.bidder] += highestBid.amount;
                auction.status = Status.Expired;
                emit AuctionEndedWithoutWinner(auctionId, auction.reservePrice);
            }
        } else {
            emit AuctionEndedWithoutWinner(auctionId, auction.reservePrice);
        }
        activeAuctionsByNFT[auction.assetAddress][auction.assetRecordId] = 0;
    }

    function cancelAuction(uint auctionId) public onlyAuctionOwner(auctionId) {
        Auction storage auction = auctions[auctionId];
        if (auction.status != Status.Active) {
            revert AuctionIsNotActive(auctionId);
        }

        if (hasBid(auction)) {
            pendingReturns[auction.highestBid.bidder] += auction.highestBid.amount;
        }

        IERC721 token = IERC721(auction.assetAddress);
        token.transferFrom(address(this), msg.sender, auction.assetRecordId);

        auction.status = Status.Canceled;
        emit AuctionCanceled(auctionId);
    }

    function withdraw(uint amount) public {
        uint maximumReturn = pendingReturns[msg.sender];
        if (amount <= 0 || amount > maximumReturn) {
            revert InsufficientAmount(maximumReturn);
        }
        
        if (amount > 0) {
            pendingReturns[msg.sender] = 0;

            (bool success,) = msg.sender.call{value: amount}("");
            require(success, "Transfer failed.");
            emit AuctionWithdrawn(msg.sender, amount);
        }
    }

    function withdrawNFT(uint auctionId) public onlyAuctionOwner(auctionId) {
        Auction storage auction = auctions[auctionId];

        // Ensure the auction is not active
        require(auction.status == Status.Expired || auction.status == Status.Canceled, "Cannot withdraw NFT from an active auction");

        IERC721 token = IERC721(auction.assetAddress);

        // Ensure the auction contract still owns the NFT
        if (token.ownerOf(auction.assetRecordId) != address(this))
        revert InsufficientPermissions();

        // Transfer the NFT back to the owner
        token.transferFrom(address(this), auction.sellerAddress, auction.assetRecordId);
    }

    // TODO think if those functions are needed
    // function getActiveAuctionsByNFT(address contractAddress) external view returns (mapping memory) {
    //     return activeAuctionsByNFT[contractAddress];
    // }

    function getBidByAuctionId(uint _auctionId) external view returns (Bid[] memory) {
        return bidsByAuctionId[_auctionId];
    }

    function isAuctionOwner(address _user, uint _auctionId) private view returns (bool) {
        return auctions[_auctionId].sellerAddress == _user;
    }

    function hasBid(Auction memory auction) private pure returns (bool) {
        return auction.highestBid.bidder != address(0);
    }
}
