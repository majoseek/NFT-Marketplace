import { useState } from 'react';
import SearchIcon from '@/assets/icons/searchIcon.svg';
import { useNavigate } from 'react-router-dom';
import { capitalize } from 'lodash';
import { Auction, AuctionsResponse, STATUSES } from '@/types/api/auctions';
import { useQuery } from 'react-query';
import { API_KEYS } from '@/api';
import axios from 'axios';
import fileUnknown from '@/assets/images/fileUnknown.svg';
import moment from 'moment';

const NftsPage = () => {
    const navigate = useNavigate();
    const [nameFilter, setNameFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState<Auction['status'] | 'all'>(
        'all'
    );
    const { data: auctions } = useQuery(API_KEYS.AUCTIONS, () =>
        axios
            .get<AuctionsResponse>('/api/auction?page=1&count=20')
            .then((res) => res.data.auctions)
    );

    const handleAuctionClick = (auctionId: number) => {
        navigate(`/browse/${auctionId}`);
    };

    const handleOwnedNftsClick = () => navigate(`/ownedNfts`);

    const handleSearchInput = (e: React.ChangeEvent<HTMLInputElement>) => {
        setNameFilter(e.target.value);
    };

    const handleStatusFilterChange = (
        e: React.ChangeEvent<HTMLSelectElement>
    ) => {
        setStatusFilter(e.target.value as Auction['status'] | 'all');
    };

    const getStatusClassName = (status: Auction['status']) => {
        if (status === 'active') return 'text-green-400';
        else if (
            status === 'completed' ||
            status === 'expired' ||
            status === 'cancelled'
        )
            return 'text-red-500';
        return 'text-orange-500';
    };

    return (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            <div className="flex justify-around w-full">
                <span>
                    <h3 className="text-3xl font-bold">Available auctions</h3>
                    <h4 className="text-xl mt-3">
                        Explore and buy items from other users
                    </h4>
                </span>
                <div className="flex flex-row gap-5">
                    <select
                        className="select select-bordered w-32"
                        onChange={handleStatusFilterChange}
                        value={statusFilter}
                    >
                        <option value="all">All</option>
                        {Object.values(STATUSES).map((status) => (
                            <option value={status} key={status}>
                                {capitalize(status)}
                            </option>
                        ))}
                    </select>
                    <span className="relative">
                        <input
                            type="text"
                            placeholder="Search..."
                            className="input input-bordered w-full max-w-xs bg-transparent pr-12"
                            onChange={handleSearchInput}
                        />
                        <img
                            src={SearchIcon}
                            className="absolute top-3 right-3 cursor-pointer"
                            alt="search-icon"
                        />
                    </span>
                </div>
            </div>
            <section className="flex gap-10 mt-32 flex-wrap justify-center w-full">
                {auctions && auctions.length > 0 ? (
                    auctions
                        .filter((auction) => {
                            if (nameFilter !== '' && statusFilter !== 'all')
                                return (
                                    auction.title.includes(nameFilter) &&
                                    auction.status === statusFilter
                                );
                            else if (nameFilter !== '')
                                return auction.title.includes(nameFilter);
                            else if (statusFilter !== 'all')
                                return auction.status === statusFilter;
                            return true;
                        })
                        .map(
                            ({
                                auctionID,
                                description,
                                expiryTime,
                                highestBid,
                                nft,
                                status,
                                title,
                            }) => (
                                <div
                                    className="cursor-pointer"
                                    key={auctionID}
                                    onClick={() =>
                                        handleAuctionClick(auctionID)
                                    }
                                >
                                    <img
                                        src={nft.url ? nft.url : fileUnknown}
                                        alt="nft"
                                        className="rounded-t-xl h-80 w-80 max-w-xs"
                                    />
                                    <div className="bg-primary p-5 rounded-b-xl text-center hover:bg-primaryHover group">
                                        <span className="font-bold text-lg">
                                            {title}
                                        </span>
                                        <span className="flex mt-5 gap-3 leading-xs items-center font-light font-mono">
                                            {description}
                                        </span>
                                        <div className="flex justify-between mt-5 font-mono">
                                            <span className="flex gap-1 flex-col">
                                                <span className="text-gray text-center group-hover:text-white font-semibold">
                                                    Status
                                                </span>
                                                <span
                                                    className={getStatusClassName(
                                                        status
                                                    )}
                                                >
                                                    {capitalize(status)}
                                                </span>
                                            </span>
                                            <span className="flex gap-1 flex-col">
                                                <span className="text-gray group-hover:text-white font-semibold">
                                                    Highest bid
                                                </span>
                                                {highestBid.amount > 0 ? (
                                                    <span>{`${highestBid.amount}$`}</span>
                                                ) : (
                                                    <span>none!</span>
                                                )}
                                            </span>
                                        </div>
                                        <div className="flex flex-col items-center justify-center mt-5 font-mono">
                                            <span className="flex gap-1 flex-col">
                                                <span className="text-gray text-center group-hover:text-white font-semibold">
                                                    Expires on
                                                </span>
                                                <span>
                                                    {moment(expiryTime).format(
                                                        'MMM Do YYYY, h:mm:ss a'
                                                    )}
                                                </span>
                                            </span>
                                            <span className="flex gap-1 flex-col">
                                                <span className="text-gray group-hover:text-white font-semibold">
                                                    Bidded on
                                                </span>
                                                {moment(
                                                    highestBid.timestamp
                                                ).format(
                                                    'MMM Do YYYY, h:mm:ss a'
                                                )}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            )
                        )
                ) : (
                    <div className="flex flex-col justify-center items-center bg-black/20 p-10 rounded-xl">
                        {auctions === undefined ? (
                            <progress className="progress w-56" />
                        ) : (
                            <>
                                <p className="text-2xl font-bold">
                                    No auctions found!
                                </p>
                                <p className="text-xl font-light">
                                    Want to sell an item? Take a look at your
                                    <span
                                        className="link link-primary link-hover"
                                        onClick={handleOwnedNftsClick}
                                    >
                                        owned items.
                                    </span>
                                    .
                                </p>
                            </>
                        )}
                    </div>
                )}
            </section>
        </main>
    );
};

export default NftsPage;
