import { useState } from 'react';
import authorImg from '@/assets/images/userAvatar.png';
import SearchIcon from '@/assets/icons/searchIcon.svg';
import { useNavigate } from 'react-router-dom';
import { capitalize } from 'lodash';
import { Auction } from '@/types/api/auctions';

type AuctionFilter = 'all' | 'active' | 'won' | 'expired';

const NftsPage = () => {
    const navigate = useNavigate();
    const [auctions, setAuctions] = useState<Auction[]>();
    const [nameFilter, setNameFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState<AuctionFilter>('active');

    const handleAuctionClick = (auctionId: number) => {};

    const handleOwnedNftsClick = () => navigate(`/ownedNfts`);

    const handleSearchInput = (e: any) => {
        setNameFilter(e.target.value);
    };

    const handleStatusFilterChange = (e: any) => {};

    const getStatusClassName = (status: Auction['status']) => {
        if (status === 'active') return 'text-green-400';
        else if (status === 'completed' || status === 'expired')
            return 'text-red-500';
        return '';
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
                        defaultValue="active"
                    >
                        <option value="all">All</option>
                        <option value="active">Active</option>
                        <option value="won">Won</option>
                        <option value="expired">Ended</option>
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
                    auctions.map(
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
                                onClick={() => handleAuctionClick(auctionID)}
                            >
                                <img
                                    src={nft.url}
                                    alt="nft"
                                    className="rounded-t-xl h-80 w-80 max-w-xs"
                                />
                                <div className="bg-primary p-5 rounded-b-xl text-center hover:bg-gray">
                                    <span className="font-medium text-lg">
                                        {title}
                                    </span>
                                    <span className="flex mt-3 gap-3 leading-xs items-center font-light font-mono">
                                        <img
                                            src={authorImg}
                                            alt="description"
                                        />
                                        {description}
                                    </span>
                                    <div className="flex justify-between mt-5 font-mono">
                                        <span className="flex gap-1 flex-col">
                                            <span className="text-gray text-center">
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
                                            <span className="text-gray">
                                                Highest bid
                                            </span>
                                            {highestBid.amount > 0 ? (
                                                <span>{`${highestBid.amount}$`}</span>
                                            ) : (
                                                <span className="text-gray">
                                                    none!
                                                </span>
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
