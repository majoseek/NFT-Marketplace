import { useEffect, useState } from 'react';
import authorImg from '../../assets/images/userAvatar.png';
import SearchIcon from '../../assets/icons/searchIcon.svg';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { API_KEYS } from '../../api/API_KEYS';
import { useQuery } from '@tanstack/react-query';
import { getIpfsImage } from '../../utils/ipfsImageGetter';
import { useMarketplaceStore } from '../../stores/MarketplaceStore';
import { capitalize } from 'lodash';

type AuctionFilter = 'all' | 'active' | 'won' | 'expired';

const NftsPage = () => {
    const navigate = useNavigate();
    const marketplaceStore = useMarketplaceStore();
    const { schoolId } = useParams<{ schoolId: string }>();
    const [auctions, setAuctions] = useState([]);
    const [nameFilter, setNameFilter] = useState('');
    const [statusFilter, setStatusFilter] = useState<AuctionFilter>('active');
    const { data: schoolResponse, isLoading } = useQuery(
        [API_KEYS.GET_SCHOOL_INFO],
        () => axios.get(`/api/school/${schoolId}`).then((response) => response),
        { onSuccess: (response) => handleFetchSuccess(response.data.auctions) }
    );

    const handleAuctionClick = (auctionId: number) =>
        navigate(`/browse/${schoolId}/${auctionId}`);

    const handleOwnedNftsClick = () => navigate(`/ownedNfts`);

    const getMappedAndFilteredNfts = () => {
        if (isLoading || !schoolResponse?.data.auctions) return [];
        return schoolResponse!.data.auctions
            .map((auction: any) => ({
                name: auction.nftName,
                fileUri: auction.nftUri,
                nftId: auction.nftId,
                status: auction.status,
                author: auction.nftIssuer,
                auctionId: auction.auctionId,
                currentBidPrice: auction.lastBid?.price ?? 0,
            }))
            .filter(
                (auction: any) =>
                    auction.status.toLowerCase() === statusFilter ||
                    statusFilter === 'all'
            )
            .filter((auction: any) => {
                if (nameFilter.length === 0) return true;
                return auction.name
                    .toLowerCase()
                    .includes(nameFilter.toLowerCase());
            });
    };

    const handleFetchSuccess = (nftsData: any) => {
        setAuctions(getMappedAndFilteredNfts());
    };

    const handleSearchInput = (e: any) => {
        setNameFilter(e.target.value);
    };

    const handleStatusFilterChange = (e: any) => {
        setStatusFilter(e.target.value);
    };

    useEffect(() => {
        if (isLoading) return;
        setAuctions(getMappedAndFilteredNfts());
    }, [nameFilter, statusFilter, schoolResponse]);

    const getStatusClassName = (status: string) => {
        if (status === 'WON') return 'text-green-400';
        else if (status === 'ENDED' || status === 'EXPIRED')
            return 'text-red-500';
        return '';
    };

    // when user visits a link from a different school, change it in our store too
    useEffect(() => {
        const parsedSchoolId = parseInt(schoolId!);
        if (parsedSchoolId !== marketplaceStore.schoolId) {
            marketplaceStore.setChosenSchool(parsedSchoolId);
        }
    }, [schoolId]);

    return (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            <div className="flex justify-around w-full">
                <span>
                    <h3 className="text-3xl font-bold">Available auctions</h3>
                    <h4 className="text-xl mt-3">
                        Explore and buy items from students of{' '}
                        <span className="font-bold">
                            {schoolResponse?.data.name}
                        </span>
                    </h4>
                </span>
                <div className="flex flex-row gap-5">
                    <select
                        className="select select-bordered w-32"
                        onChange={handleStatusFilterChange}
                        disabled={schoolResponse?.data.auctions.length === 0}
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
                            disabled={
                                schoolResponse?.data.auctions.length === 0
                            }
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
                {!isLoading && auctions.length > 0 ? (
                    auctions.map(
                        ({
                            auctionId,
                            name,
                            fileUri,
                            author,
                            currentBidPrice,
                            status,
                        }) => (
                            <div
                                className="cursor-pointer"
                                key={auctionId}
                                onClick={() => handleAuctionClick(auctionId)}
                            >
                                <img
                                    src={getIpfsImage(fileUri)}
                                    alt="nft"
                                    className="rounded-t-xl h-80 w-80 max-w-xs"
                                />
                                <div className="bg-primary p-5 rounded-b-xl text-center hover:bg-gray">
                                    <span className="font-medium text-lg">
                                        {name}
                                    </span>
                                    <span className="flex mt-3 gap-3 leading-xs items-center font-light font-mono">
                                        <img src={authorImg} alt="author" />
                                        {author}
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
                                            {currentBidPrice > 0 ? (
                                                <span>{`${currentBidPrice}$`}</span>
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
                        {isLoading ? (
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
                                        onClick={() => handleOwnedNftsClick()}
                                    >
                                        {' '}
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
