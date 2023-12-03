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
    auctionId: number;
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
    owner: string;
    expiryTime: string;
    highestBid: {
        bidder: string;
        amount: number;
        timestamp: string;
    } | null;
    status: (typeof STATUSES)[keyof typeof STATUSES];
};

export type AuctionsResponse = {
    auctions: Auction[];
    page: number;
    size: number;
    count: number;
};

export type AuctionDetailsResponse = Omit<Auction, 'highestBid'> & {
    winner: string;
    bids: {
        bidder: string;
        amount: number;
        timestamp: string;
    }[];
    startingPrice: number;
    minimumIncrement: number;
};
