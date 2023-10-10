import { useEffect, useState } from 'react';
import axios from 'axios';
import * as Styled from './Auctions.styles';
import AuctionInfoModal from './AuctionInfoModal';
import { AuctionResponse } from '@/api/types/auction';
import Button from '@/components/Button';
import { Dropdown, MenuProps } from 'antd';
import { DownOutlined, FilterOutlined } from '@ant-design/icons';

const Auctions = () => {
    const [auctions, setAuctions] = useState<AuctionResponse['auctions']>([]);
    const [isAuctionModalInfoVisible, setIsAuctionModalInfoVisible] =
        useState(false);
    const [selectedAuctionId, setSelectedAuctionId] = useState<number | null>(
        null
    );
    const [currentSortValue, setCurrentSortValue] = useState('Newest');

    const handleSortItemClick = (sortValue: string) => {
        setCurrentSortValue(sortValue);
    };

    const items: MenuProps['items'] = [
        {
            key: 'newest',
            label: <Styled.FilterLabel>Newest</Styled.FilterLabel>,
            onClick: () => handleSortItemClick('Newest'),
        },
        {
            key: 'lowest_price',
            label: <Styled.FilterLabel>Price:Low to High</Styled.FilterLabel>,
            onClick: () => handleSortItemClick('Price:Low to High'),
        },
        {
            key: 'highest_price',
            label: <Styled.FilterLabel>Price:High to Low</Styled.FilterLabel>,
            onClick: () => handleSortItemClick('Price:High to Low'),
        },
        {
            key: 'ending',
            label: <Styled.FilterLabel>Ending soon</Styled.FilterLabel>,
            onClick: () => handleSortItemClick('Ending soon'),
        },
    ];

    useEffect(() => {
        axios
            .get('/auction?page=1&count=20&status=Active')
            .then((res: { data: AuctionResponse }) => {
                setAuctions(res.data.auctions);
            });
    }, []);

    const handlePlaceBid = (auctionId: number) => {
        setIsAuctionModalInfoVisible(true);
        setSelectedAuctionId(auctionId);
    };

    return (
        <Styled.AuctionsContainer>
            <Styled.DiscoverWrapper>
                <Styled.TitlesWrapper>
                    <Styled.DiscoverTitle>Discover</Styled.DiscoverTitle>
                    <Styled.DiscoverDescription>
                        151,146 items listed
                    </Styled.DiscoverDescription>
                </Styled.TitlesWrapper>
                <Styled.ActionsWrapper>
                    <Dropdown menu={{ items }}>
                        <Styled.DropdownBtn>
                            {currentSortValue}
                            <DownOutlined />
                        </Styled.DropdownBtn>
                    </Dropdown>
                    <Styled.FilterBtn icon={<FilterOutlined />}>
                        Filter
                    </Styled.FilterBtn>
                </Styled.ActionsWrapper>
            </Styled.DiscoverWrapper>
            {auctions.map((auction) => (
                <div key={auction.auctionID}>
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
