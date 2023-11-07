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
    status: 'active' | 'completed' | 'expired' | 'ending' | 'cancelled';
};

export type AuctionsResponse = {
    auctions: Auction[];
    page: number;
    size: number;
    count: number;
};
