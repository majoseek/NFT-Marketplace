export const address = '0x647E1F5a936e0b30751Af18078E20413865f9A91';

export const abi = [
    { inputs: [], stateMutability: 'nonpayable', type: 'constructor' },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'uint256',
                name: 'id',
                type: 'uint256',
            },
        ],
        name: 'AuctionCancelled',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'uint256',
                name: 'id',
                type: 'uint256',
            },
            {
                indexed: false,
                internalType: 'string',
                name: 'title',
                type: 'string',
            },
            {
                indexed: false,
                internalType: 'uint128',
                name: 'startingPrice',
                type: 'uint128',
            },
            {
                indexed: false,
                internalType: 'uint128',
                name: 'reservePrice',
                type: 'uint128',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'expiryTime',
                type: 'uint256',
            },
        ],
        name: 'AuctionCreated',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'uint256',
                name: 'auctionId',
                type: 'uint256',
            },
            {
                indexed: false,
                internalType: 'address',
                name: 'winningBidder',
                type: 'address',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'amount',
                type: 'uint256',
            },
        ],
        name: 'AuctionEndedWithWinner',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'uint256',
                name: 'auctionId',
                type: 'uint256',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'topBid',
                type: 'uint256',
            },
            {
                indexed: false,
                internalType: 'uint128',
                name: 'reservePrice',
                type: 'uint128',
            },
        ],
        name: 'AuctionEndedWithoutWinner',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'uint256',
                name: 'auctionId',
                type: 'uint256',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'newExpiryTime',
                type: 'uint256',
            },
        ],
        name: 'AuctionExtended',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'address',
                name: 'withdrawer',
                type: 'address',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'amount',
                type: 'uint256',
            },
        ],
        name: 'AuctionWithdrawn',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'uint256',
                name: 'auctionId',
                type: 'uint256',
            },
            {
                indexed: false,
                internalType: 'address',
                name: 'bidder',
                type: 'address',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'amount',
                type: 'uint256',
            },
        ],
        name: 'BidPlaced',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'string',
                name: 'message',
                type: 'string',
            },
        ],
        name: 'LogFailure',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: false,
                internalType: 'address',
                name: 'from',
                type: 'address',
            },
            {
                indexed: false,
                internalType: 'address',
                name: 'to',
                type: 'address',
            },
            {
                indexed: false,
                internalType: 'uint256',
                name: 'tokenId',
                type: 'uint256',
            },
        ],
        name: 'NFTTransferred',
        type: 'event',
    },
    {
        inputs: [],
        name: 'auctionCount',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        name: 'auctions',
        outputs: [
            {
                internalType: 'uint256',
                name: 'auctionRecordId',
                type: 'uint256',
            },
            { internalType: 'string', name: 'title', type: 'string' },
            { internalType: 'string', name: 'description', type: 'string' },
            { internalType: 'address', name: 'assetAddress', type: 'address' },
            { internalType: 'uint256', name: 'assetRecordId', type: 'uint256' },
            { internalType: 'uint128', name: 'startingPrice', type: 'uint128' },
            { internalType: 'uint128', name: 'reservePrice', type: 'uint128' },
            {
                internalType: 'uint128',
                name: 'minimumIncrement',
                type: 'uint128',
            },
            { internalType: 'uint8', name: 'distributionCut', type: 'uint8' },
            { internalType: 'uint256', name: 'expiryTime', type: 'uint256' },
            {
                internalType: 'enum NFTAuction.Status',
                name: 'status',
                type: 'uint8',
            },
            { internalType: 'address', name: 'sellerAddress', type: 'address' },
            {
                components: [
                    {
                        internalType: 'address',
                        name: 'bidder',
                        type: 'address',
                    },
                    {
                        internalType: 'uint256',
                        name: 'amount',
                        type: 'uint256',
                    },
                    {
                        internalType: 'uint256',
                        name: 'timestamp',
                        type: 'uint256',
                    },
                ],
                internalType: 'struct NFTAuction.Bid',
                name: 'highestBid',
                type: 'tuple',
            },
        ],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'address', name: '', type: 'address' },
            { internalType: 'uint256', name: '', type: 'uint256' },
        ],
        name: 'auctionsByNFT',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: '', type: 'uint256' },
            { internalType: 'uint256', name: '', type: 'uint256' },
        ],
        name: 'auctionsByStatus',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: '', type: 'uint256' },
            { internalType: 'uint256', name: '', type: 'uint256' },
        ],
        name: 'bidsByAuctionId',
        outputs: [
            { internalType: 'address', name: 'bidder', type: 'address' },
            { internalType: 'uint256', name: 'amount', type: 'uint256' },
            { internalType: 'uint256', name: 'timestamp', type: 'uint256' },
        ],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        name: 'bidsCountByAuction',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: 'auctionId', type: 'uint256' },
        ],
        name: 'cancelAuction',
        outputs: [],
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        inputs: [],
        name: 'contractOwner',
        outputs: [{ internalType: 'address', name: '', type: 'address' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'string', name: 'title', type: 'string' },
            { internalType: 'string', name: 'description', type: 'string' },
            { internalType: 'address', name: 'assetAddress', type: 'address' },
            { internalType: 'uint256', name: 'assetRecordId', type: 'uint256' },
            { internalType: 'uint128', name: 'startingPrice', type: 'uint128' },
            { internalType: 'uint128', name: 'reservePrice', type: 'uint128' },
            {
                internalType: 'uint128',
                name: 'minimumIncrement',
                type: 'uint128',
            },
            { internalType: 'uint128', name: 'duration', type: 'uint128' },
            { internalType: 'uint8', name: 'distributionCut', type: 'uint8' },
        ],
        name: 'createAuction',
        outputs: [],
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: 'auctionId', type: 'uint256' },
        ],
        name: 'endAuction',
        outputs: [],
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: 'auctionId', type: 'uint256' },
            {
                internalType: 'uint256',
                name: 'additionalTime',
                type: 'uint256',
            },
        ],
        name: 'extendAuctionTime',
        outputs: [],
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: 'auctionId', type: 'uint256' },
        ],
        name: 'getAuctionStatus',
        outputs: [
            { internalType: 'enum NFTAuction.Status', name: '', type: 'uint8' },
        ],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            {
                internalType: 'address',
                name: 'contractAddress',
                type: 'address',
            },
        ],
        name: 'getAuctionsByNFT',
        outputs: [{ internalType: 'uint256[]', name: '', type: 'uint256[]' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            {
                internalType: 'enum NFTAuction.Status',
                name: '_status',
                type: 'uint8',
            },
        ],
        name: 'getAuctionsByStatus',
        outputs: [{ internalType: 'uint256[]', name: '', type: 'uint256[]' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [{ internalType: 'address', name: 'user', type: 'address' }],
        name: 'getAuctionsByUser',
        outputs: [{ internalType: 'uint256[]', name: '', type: 'uint256[]' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: '_auctionId', type: 'uint256' },
        ],
        name: 'getBidByAuctionId',
        outputs: [
            {
                components: [
                    {
                        internalType: 'address',
                        name: 'bidder',
                        type: 'address',
                    },
                    {
                        internalType: 'uint256',
                        name: 'amount',
                        type: 'uint256',
                    },
                    {
                        internalType: 'uint256',
                        name: 'timestamp',
                        type: 'uint256',
                    },
                ],
                internalType: 'struct NFTAuction.Bid[]',
                name: '',
                type: 'tuple[]',
            },
        ],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'address', name: '', type: 'address' },
            { internalType: 'uint256', name: '', type: 'uint256' },
        ],
        name: 'nftToAuction',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [{ internalType: 'address', name: '', type: 'address' }],
        name: 'pendingReturns',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        stateMutability: 'view',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: 'auctionId', type: 'uint256' },
        ],
        name: 'placeBid',
        outputs: [],
        stateMutability: 'payable',
        type: 'function',
    },
    {
        inputs: [],
        name: 'withdraw',
        outputs: [],
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        inputs: [
            { internalType: 'uint256', name: 'auctionId', type: 'uint256' },
        ],
        name: 'withdrawNFT',
        outputs: [],
        stateMutability: 'nonpayable',
        type: 'function',
    },
];

export const nftAbi = [
    {
        constant: false,
        inputs: [
            { internalType: 'address', name: 'to', type: 'address' },
            { internalType: 'uint256', name: 'tokenId', type: 'uint256' },
        ],
        name: 'approve',
        outputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        constant: false,
        inputs: [
            { internalType: 'address', name: 'to', type: 'address' },
            { internalType: 'uint256', name: 'tokenId', type: 'uint256' },
        ],
        name: 'mint',
        outputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        constant: false,
        inputs: [
            { internalType: 'address', name: 'from', type: 'address' },
            { internalType: 'address', name: 'to', type: 'address' },
            { internalType: 'uint256', name: 'tokenId', type: 'uint256' },
        ],
        name: 'safeTransferFrom',
        outputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        constant: false,
        inputs: [
            { internalType: 'address', name: 'from', type: 'address' },
            { internalType: 'address', name: 'to', type: 'address' },
            { internalType: 'uint256', name: 'tokenId', type: 'uint256' },
            { internalType: 'bytes', name: '_data', type: 'bytes' },
        ],
        name: 'safeTransferFrom',
        outputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        constant: false,
        inputs: [
            { internalType: 'address', name: 'to', type: 'address' },
            { internalType: 'bool', name: 'approved', type: 'bool' },
        ],
        name: 'setApprovalForAll',
        outputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        constant: false,
        inputs: [
            { internalType: 'address', name: 'from', type: 'address' },
            { internalType: 'address', name: 'to', type: 'address' },
            { internalType: 'uint256', name: 'tokenId', type: 'uint256' },
        ],
        name: 'transferFrom',
        outputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'function',
    },
    {
        inputs: [],
        payable: false,
        stateMutability: 'nonpayable',
        type: 'constructor',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: true,
                internalType: 'address',
                name: 'from',
                type: 'address',
            },
            {
                indexed: true,
                internalType: 'address',
                name: 'to',
                type: 'address',
            },
            {
                indexed: true,
                internalType: 'uint256',
                name: 'tokenId',
                type: 'uint256',
            },
        ],
        name: 'Transfer',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: true,
                internalType: 'address',
                name: 'owner',
                type: 'address',
            },
            {
                indexed: true,
                internalType: 'address',
                name: 'approved',
                type: 'address',
            },
            {
                indexed: true,
                internalType: 'uint256',
                name: 'tokenId',
                type: 'uint256',
            },
        ],
        name: 'Approval',
        type: 'event',
    },
    {
        anonymous: false,
        inputs: [
            {
                indexed: true,
                internalType: 'address',
                name: 'owner',
                type: 'address',
            },
            {
                indexed: true,
                internalType: 'address',
                name: 'operator',
                type: 'address',
            },
            {
                indexed: false,
                internalType: 'bool',
                name: 'approved',
                type: 'bool',
            },
        ],
        name: 'ApprovalForAll',
        type: 'event',
    },
    {
        constant: true,
        inputs: [{ internalType: 'address', name: 'owner', type: 'address' }],
        name: 'balanceOf',
        outputs: [{ internalType: 'uint256', name: '', type: 'uint256' }],
        payable: false,
        stateMutability: 'view',
        type: 'function',
    },
    {
        constant: true,
        inputs: [{ internalType: 'uint256', name: 'tokenId', type: 'uint256' }],
        name: 'getApproved',
        outputs: [{ internalType: 'address', name: '', type: 'address' }],
        payable: false,
        stateMutability: 'view',
        type: 'function',
    },
    {
        constant: true,
        inputs: [
            { internalType: 'address', name: 'owner', type: 'address' },
            { internalType: 'address', name: 'operator', type: 'address' },
        ],
        name: 'isApprovedForAll',
        outputs: [{ internalType: 'bool', name: '', type: 'bool' }],
        payable: false,
        stateMutability: 'view',
        type: 'function',
    },
    {
        constant: true,
        inputs: [{ internalType: 'uint256', name: 'tokenId', type: 'uint256' }],
        name: 'ownerOf',
        outputs: [{ internalType: 'address', name: '', type: 'address' }],
        payable: false,
        stateMutability: 'view',
        type: 'function',
    },
    {
        constant: true,
        inputs: [
            { internalType: 'bytes4', name: 'interfaceId', type: 'bytes4' },
        ],
        name: 'supportsInterface',
        outputs: [{ internalType: 'bool', name: '', type: 'bool' }],
        payable: false,
        stateMutability: 'view',
        type: 'function',
    },
];
