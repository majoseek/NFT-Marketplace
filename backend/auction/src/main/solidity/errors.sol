// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

error InsufficientAmount(uint256 amount);
error InsufficientPermissions();
error AuctionDoesNotExist(uint256 auctionId);
error ActiveAuctionWithNFTAlreadyExists(address nftAddress, uint256 nftTokenId);
error AuctionIsNotActive(uint256 auctionId);
