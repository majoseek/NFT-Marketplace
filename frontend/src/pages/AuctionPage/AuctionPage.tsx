import { useState } from 'react';
import { MinimalNft } from '../OwnedNftsPage/OwnedNftsPage';
import moment from 'moment';

type Bid = {
    auctionId: number;
    createdAt: string;
    bidder: {
        accountId: number;
        name: string;
    };
    price: number;
};

type Nft = {
    nftId: number;
    name: string;
    description: string;
    uri: string;
    mintedDate: string;
    isImage: boolean;
    issuer: {
        accountId: number;
        name: string;
    };
    owner: {
        accountId: number;
        name: string;
    };
    tags: {
        tagId: number;
        name: string;
    }[];
};

const AuctionPage = () => {
    const [bid, setBid] = useState(0);
    const [auction, setAuction] = useState();
    const [nft, setNft] = useState<Nft>();
    const auctionEnded = false;

    return true ? (
        <main className="py-32 px-20 flex justify-center gap-10">
            <section className="flex flex-col gap-3">
                <img src="cos" alt="nft" className="rounded-xl w-96" />
                <h2 className="font-bold text-4xl">{nft?.name}</h2>
                <span className="text-gray">
                    {' '}
                    Minted on {moment('').format('lll')}
                </span>
                <span className="text-gray font-mono font-semibold text-lg">
                    Created by
                </span>
                <span className="font-mono">{nft?.issuer.name}</span>
                <span className="text-gray font-mono font-semibold text-lg">
                    Description
                </span>
                <span className="max-w-lg leading-7">
                    {auction && auction.nft.description}
                </span>
                <span className="text-gray font-mono font-semibold text-lg">
                    Tags
                </span>
                <div className="flex gap-3">
                    {nft &&
                        nft.tags.map((tag) => (
                            <button key={tag.tagId} className="btn">
                                {tag.name}
                            </button>
                        ))}
                </div>
            </section>
            <section className="p-6 flex flex-col bg-primary h-fit rounded-xl gap-2 items-center w-96">
                {auctionEnded ? (
                    <span className="text-md font-mono font-bold text-error">
                        Auction ended!
                    </span>
                ) : (
                    <span className="text-xs font-mono">Auction ends in: </span>
                )}
                {!auctionEnded && (
                    <span className="text-3xl font-mono -mt-2">
                        {2}:{32}:{59}
                    </span>
                )}
                <span className="mt-3 text-lg font-mono">
                    {auction && auction.currentPrice > 0 ? (
                        <>
                            {auctionEnded ? 'Winning bid' : 'Current price'}
                            {': '}
                            <span className="font-bold">
                                {auction?.currentPrice}$
                            </span>
                        </>
                    ) : (
                        <>{auctionEnded ? 'No one bidded :(' : 'No bids yet'}</>
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
                        auction && auction.currentPrice !== null
                            ? bid <= auction.currentPrice
                            : true
                    }
                >
                    {auctionEnded ? 'Auction ended' : 'Place bid'}
                </button>

                <div className="flex flex-col gap-2 mt-3 w-full">
                    <span className="font-bold">Bids history</span>
                    <div className="flex flex-col gap-2">
                        {auction && auction?.bids.length > 0 ? (
                            auction?.bids
                                .sort((a, b) => b.price - a.price)
                                .map((bid) => (
                                    <div
                                        key={`${bid.createdAt}_${bid.bidder}_${bid.price}`}
                                        className={`flex justify-between p-4 rounded-xl  ${
                                            bid.price === auction.currentPrice
                                                ? 'bg-green-900/50 text-xl text-green-400/90'
                                                : 'bg-gray/10'
                                        }`}
                                    >
                                        <span className="font-bold">
                                            {bid.bidder.name}
                                        </span>
                                        <span
                                            className={`font-mono ${
                                                bid.price ===
                                                    auction.currentPrice &&
                                                'font-bold'
                                            }`}
                                        >
                                            {bid.price}$
                                        </span>
                                    </div>
                                ))
                        ) : (
                            <span className="text-gray">No bids yet!</span>
                        )}
                    </div>
                </div>
            </section>
        </main>
    ) : (
        <progress className="progress w-56" />
    );
};

export default AuctionPage;
