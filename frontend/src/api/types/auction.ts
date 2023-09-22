type NftToken = {
    address: string;
    tokenID: number;
};

type BidResponse = {
    bidder: string;
    amount: number;
    timestamp: string;
};

type NftStatus = 'pending' | 'active' | 'canceled' | 'expired';

export type AuctionResponse = {
    auctions: {
        auctionID: number;
        title: string;
        description: string;
        nft: NftToken;
        startingPrice: number;
        reservePrice: number;
        minimumIncrement: number;
        expiryTime: string;
        highestBid?: BidResponse;
        status: NftStatus;
    }[];
    page: number;
    size: number;
    count: number;
};

export type AuctionDetails = {
    auctionID: number;
    title: string;
    description: string;
    nft: {
        contractAddress: string;
        tokenID: number;
        name: string;
        ownerAddress: null;
        url: string;
        description: string;
        type: string;
    };
    startingPrice: number;
    reservePrice: number;
    minimumIncrement: number;
    expiryTime: string;
    bids: { bidder: string; amount: number }[];
    status: string;
};
