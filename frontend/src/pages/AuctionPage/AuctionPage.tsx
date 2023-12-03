import { useEffect, useMemo, useState } from 'react';
import moment from 'moment';
import { AuctionDetailsResponse } from '@/types/api/auctions';
import LoadingElement from '@/components/LoadingElement';
import { useQuery } from 'react-query';
import { API_KEYS } from '@/api';
import { useParams } from 'react-router-dom';
import fileUnknown from '@/assets/images/fileUnknown.svg';
import axios from 'axios';
import { convertEthToWei } from '@/utils/helpers';

const AuctionPage = () => {
    const [bid, setBid] = useState<number>();
    const { auctionId } = useParams<{ auctionId: string }>();
    const [currentTime, setCurrentTime] = useState<moment.Moment>(moment());

    const { data: auction } = useQuery(
        API_KEYS.AUCTION_DETAILS,
        () =>
            axios
                .get<AuctionDetailsResponse>(`/api/auction/${auctionId}`)
                .then((res) => res.data),
        { enabled: auctionId !== undefined }
    );

    const timeDifference = useMemo(() => {
        if (!auction) return;

        const expirationTime = moment(auction.expiryTime);
        return moment.duration(expirationTime.diff(currentTime));
    }, [auction, currentTime]);

    const auctionIsOver = useMemo(() => {
        if (!timeDifference) return false;

        return timeDifference.asMilliseconds() <= 0;
    }, [timeDifference]);

    const formattedTimeDifference = useMemo(
        () =>
            timeDifference &&
            moment.utc(timeDifference.asMilliseconds()).format('HH:mm:ss'),
        [timeDifference]
    );

    const handleBidChange: React.ChangeEventHandler<HTMLInputElement> = (e) => {
        setBid(Number(e.target.value));
    };

    useEffect(() => {
        const intervalId = setInterval(() => {
            setCurrentTime(moment());
        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    return (
        <main className="py-32 px-20 flex justify-center gap-10">
            {auction ? (
                <>
                    <section className="flex flex-col gap-3 h-fit border-2 border-primary rounded-xl p-6">
                        <img
                            src={
                                auction.nft.url ? auction.nft.url : fileUnknown
                            }
                            alt="nft"
                            className="rounded-xl w-96"
                        />
                        <h2 className="font-bold text-3xl">
                            {auction.nft.name}
                        </h2>
                        <span className="text-gray font-mono font-semibold text-lg">
                            Expires on
                        </span>
                        <span className="font-mono">
                            {moment(auction.expiryTime).format(
                                'MMM Do YYYY, h:mm:ss a'
                            )}
                        </span>
                        <span className="text-gray font-mono font-semibold text-lg">
                            Owned by
                        </span>
                        <span className="font-mono">{auction.owner}</span>
                        <span className="text-gray font-mono font-semibold text-lg">
                            Description
                        </span>
                        <span className="max-w-lg leading-7">
                            {auction.nft.description}
                        </span>
                    </section>
                    <section className="p-6 flex flex-col bg-primary h-fit rounded-xl gap-2 items-center w-96">
                        {auctionIsOver ? (
                            <span className="text-md font-mono font-bold text-error">
                                Auction ended!
                            </span>
                        ) : (
                            <span className="text-xs font-mono">
                                Auction ends in:
                            </span>
                        )}
                        {!auctionIsOver && (
                            <span className="text-3xl font-mono -mt-2">
                                {formattedTimeDifference}
                            </span>
                        )}
                        <span className="mt-3 text-lg font-mono">
                            {auction.bids.length > 0 ? (
                                <>
                                    {auctionIsOver
                                        ? 'Winning bid'
                                        : 'Current price'}
                                    {': '}
                                    <span className="font-bold">
                                        {convertEthToWei(
                                            auction.bids[0].amount
                                        )}
                                    </span>
                                </>
                            ) : (
                                <>
                                    {auctionIsOver
                                        ? 'No one bidded :('
                                        : 'No bids yet'}
                                </>
                            )}
                        </span>
                        <div className="flex mt-2">
                            <span className="inline-flex items-center px-3 text-sm text-gray-900 bg-gray-200 border border-r-0 border-gray-300 rounded-l-md dark:bg-gray-600 dark:text-gray-400 dark:border-gray-600">
                                ETH
                            </span>
                            <input
                                type="text"
                                className="input rounded-none rounded-r-lg bg-gray-50 border border-solid border-white text-gray-900 block flex-1 min-w-0 w-full text-sm border-gray-300 p-2.5"
                                placeholder="Type your bid"
                                onChange={handleBidChange}
                                disabled={auctionIsOver}
                            />
                        </div>
                        <button
                            onClick={() => {}}
                            className="btn btn-primary w-fit font-mono mt-3"
                            disabled={
                                auctionIsOver ||
                                bid == undefined ||
                                (auction.bids.length > 0 &&
                                    bid !== undefined &&
                                    bid < auction.bids[0].amount)
                            }
                        >
                            {auctionIsOver ? 'Auction ended' : 'Place bid'}
                        </button>

                        <div className="flex flex-col gap-2 mt-3 w-full">
                            <span className="font-bold">
                                {auction.bids.length > 10
                                    ? 'Showing 10 last bids'
                                    : 'Bids history'}
                            </span>
                            <div className="flex flex-col gap-2">
                                {auction.bids.length > 0 ? (
                                    auction.bids
                                        .filter((_, index) => index < 10)
                                        .map((bid) => (
                                            <div
                                                key={`${bid.timestamp}-${bid.amount}`}
                                                className={`flex justify-between p-4 rounded-xl  ${
                                                    bid.amount ===
                                                    auction.bids[0].amount
                                                        ? 'bg-green-900/50 text-green-400/90'
                                                        : 'bg-gray/10'
                                                }`}
                                            >
                                                <span className="font-bold">
                                                    {moment(
                                                        bid.timestamp
                                                    ).format(
                                                        'MMM Do YYYY, h:mm:ss a'
                                                    )}
                                                </span>
                                                <span
                                                    className={`font-mono ${
                                                        bid.amount ===
                                                            auction.bids[0]
                                                                .amount &&
                                                        'font-bold'
                                                    }`}
                                                >
                                                    {convertEthToWei(
                                                        bid.amount
                                                    )}
                                                </span>
                                            </div>
                                        ))
                                ) : (
                                    <span className="text-gray">
                                        No bids yet!
                                    </span>
                                )}
                            </div>
                        </div>
                    </section>
                </>
            ) : (
                <LoadingElement title="Loading auction..." className="w-full" />
            )}
        </main>
    );
};

export default AuctionPage;
