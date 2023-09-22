import { Nft, NftDetails } from './nft';

type BidResponse = {
    bidder: string;
    amount: number;
    timestamp: string;
};

type Status = 'pending' | 'active' | 'canceled' | 'expired';

export type AuctionResponse = {
    auctions: {
        auctionID: number;
        title: string;
        description: string;
        nft: Nft;
        startingPrice: number;
        reservePrice: number;
        minimumIncrement: number;
        expiryTime: string;
        highestBid?: BidResponse;
        status: Status;
    }[];
    page: number;
    size: number;
    count: number;
};

export type AuctionDetails = {
    auctionID: number;
    title: string;
    description: string;
    nft: NftDetails;
    startingPrice: number;
    reservePrice: number;
    minimumIncrement: number;
    expiryTime: string;
    bids: { bidder: string; amount: number }[];
    status: Status;
};
