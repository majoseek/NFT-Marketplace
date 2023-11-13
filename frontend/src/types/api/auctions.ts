export const STATUSES = {
    ACTIVE: 'active',
    COMPLETED: 'completed',
    EXPIRED: 'expired',
    ENDING: 'ending',
    CANCELLED: 'cancelled',
} as const;

export type Nft = {
    contractAddress: string;
    tokenId: number;
    name: string;
    ownerAddress: string;
    url: string;
    description: string;
    type: string;
};

export type Auction = {
    auctionID: number;
    title: string;
    description: string;
    nft: {
        contractAddress: string;
        tokenId: number;
        name: string;
        ownerAddress: string;
        url: string;
        description: string;
        type: string;
    };
    expiryTime: string;
    highestBid: {
        bidder: string;
        amount: number;
        timestamp: string;
    };
    status: (typeof STATUSES)[keyof typeof STATUSES];
};

export type AuctionsResponse = {
    auctions: Auction[];
    page: number;
    size: number;
    count: number;
};
