export type Auction = {
    auctionID: number;
    title: string;
    description: string;
    nft: {
        address: string;
        tokenID: number;
    };
    startingPrice: number;
    reservePrice: number;
    minimumIncrement: number;
    expiryTime: string;
    highestBid: {
        bidder: string;
        amount: number;
    };
    status: string;
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
