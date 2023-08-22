import { useEffect, useState } from 'react';
import { Auction } from '../types/auction';
import axios from 'axios';
import * as Styled from './Auctions.styles';
import { Button, Divider } from 'antd';
import AuctionInfoModal from './AuctionInfoModal';

const Auctions = () => {
    const [auctions, setAuctions] = useState<Auction[]>([]);
    const [isAuctionModalInfoVisible, setIsAuctionModalInfoVisible] =
        useState(false);
    const [selectedAuctionId, setSelectedAuctionId] = useState<number | null>(
        null
    );

    useEffect(() => {
        axios.get('/auction?page=1&count=20&status=Active').then((res: any) => {
            setAuctions(res.data.auctions);
        });
    }, []);

    const handlePlaceBid = (auctionId: number) => {
        setIsAuctionModalInfoVisible(true);
        setSelectedAuctionId(auctionId);
    };

    return (
        <Styled.AuctionsContainer>
            <h2>Available auctions</h2>
            {auctions.map((auction) => (
                <div key={auction.auctionID}>
                    <Divider />
                    <Styled.AuctionWrapper>
                        <span>Title: {auction.title}</span>
                        <span>Description: {auction.description}</span>
                        {auction.highestBid && (
                            <span>
                                Highest bid: {auction.highestBid.amount}
                            </span>
                        )}
                        <span>Expiration time: {auction.expiryTime}</span>
                        <Button
                            type="primary"
                            onClick={() => handlePlaceBid(auction.auctionID)}
                        >
                            Place bid
                        </Button>
                    </Styled.AuctionWrapper>
                </div>
            ))}
            {selectedAuctionId !== null && (
                <AuctionInfoModal
                    auctionId={selectedAuctionId}
                    visible={isAuctionModalInfoVisible}
                    setVisible={setIsAuctionModalInfoVisible}
                />
            )}
        </Styled.AuctionsContainer>
    );
};

export default Auctions;
