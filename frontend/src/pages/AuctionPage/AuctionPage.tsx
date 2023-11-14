import { useState } from 'react';
import moment from 'moment';
import { AuctionDetailsResponse } from '@/types/api/auctions';
import LoadingElement from '@/components/LoadingElement';

const AuctionPage = () => {
    const [bid, setBid] = useState(0);
    const [auction, setAuction] = useState<AuctionDetailsResponse>();
    const auctionEnded = false;

    return (
        <main className="py-32 px-20 flex justify-center gap-10">
            {auction ? (
                <>
                    <section className="flex flex-col gap-3">
                        <img src="cos" alt="nft" className="rounded-xl w-96" />
                        <h2 className="font-bold text-4xl">
                            {auction.nft.name}
                        </h2>
                        <span className="text-gray">
                            Minted on {moment('').format('lll')}
                        </span>
                        <span className="text-gray font-mono font-semibold text-lg">
                            Created by
                        </span>
                        <span className="font-mono">
                            {auction.nft.ownerAddress}
                        </span>
                        <span className="text-gray font-mono font-semibold text-lg">
                            Description
                        </span>
                        <span className="max-w-lg leading-7">
                            {auction.nft.description}
                        </span>
                    </section>
                    <section className="p-6 flex flex-col bg-primary h-fit rounded-xl gap-2 items-center w-96">
                        {auctionEnded ? (
                            <span className="text-md font-mono font-bold text-error">
                                Auction ended!
                            </span>
                        ) : (
                            <span className="text-xs font-mono">
                                Auction ends in:{' '}
                            </span>
                        )}
                        {!auctionEnded && (
                            <span className="text-3xl font-mono -mt-2">
                                {2}:{32}:{59}
                            </span>
                        )}
                        <span className="mt-3 text-lg font-mono">
                            {auction && auction.highestBid.amount > 0 ? (
                                <>
                                    {auctionEnded
                                        ? 'Winning bid'
                                        : 'Current price'}
                                    {': '}
                                    <span className="font-bold">
                                        {auction.highestBid.amount}$
                                    </span>
                                </>
                            ) : (
                                <>
                                    {auctionEnded
                                        ? 'No one bidded :('
                                        : 'No bids yet'}
                                </>
                            )}
                        </span>
                        <div className="flex mt-2">
                            <span className="inline-flex items-center px-3 text-sm text-gray-900 bg-gray-200 border border-r-0 border-gray-300 rounded-l-md dark:bg-gray-600 dark:text-gray-400 dark:border-gray-600">
                                $
                            </span>
                            <input
                                type="text"
                                className="input rounded-none rounded-r-lg bg-gray-50 border border-solid border-white text-gray-900 block flex-1 min-w-0 w-full text-sm border-gray-300 p-2.5"
                                placeholder="Type your bid"
                                onChange={() => {}}
                                disabled={auctionEnded}
                            />
                        </div>
                        <button
                            onClick={() => {}}
                            className="btn btn-primary w-fit font-mono mt-3"
                            disabled={
                                auction && auction.highestBid.amount !== null
                                    ? bid <= auction.highestBid.amount
                                    : true
                            }
                        >
                            {auctionEnded ? 'Auction ended' : 'Place bid'}
                        </button>

                        <div className="flex flex-col gap-2 mt-3 w-full">
                            <span className="font-bold">Bids history</span>
                            <div className="flex flex-col gap-2">
                                {auction.bids.length > 0 ? (
                                    auction.bids
                                        .sort((a, b) => b.amount - a.amount)
                                        .map((bid) => (
                                            <div
                                                key={`${bid.timestamp}`}
                                                className={`flex justify-between p-4 rounded-xl  ${
                                                    bid.amount ===
                                                    auction.highestBid.amount
                                                        ? 'bg-green-900/50 text-xl text-green-400/90'
                                                        : 'bg-gray/10'
                                                }`}
                                            >
                                                <span className="font-bold">
                                                    {bid.bidder}
                                                </span>
                                                <span
                                                    className={`font-mono ${
                                                        bid.amount ===
                                                            auction.highestBid
                                                                .amount &&
                                                        'font-bold'
                                                    }`}
                                                >
                                                    {bid.amount}$
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
