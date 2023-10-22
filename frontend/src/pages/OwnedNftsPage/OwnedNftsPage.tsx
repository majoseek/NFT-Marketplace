import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { API_KEYS } from '../../api/API_KEYS';
import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '../../stores/AuthStore';
import { getIpfsImage } from '../../utils/ipfsImageGetter';
import { useMarketplaceStore } from '../../stores/MarketplaceStore';

export type MinimalNft = {
    nftId: number;
    name: string;
    description: string;
    uri: string;
    isImage: boolean;
    activeAuctionId: number | null;
};

const OwnedNftsPage = () => {
    const navigate = useNavigate();
    const { accountId } = useAuthStore();
    const { schoolId } = useMarketplaceStore();

    const handleCreateNft = () => {
        navigate('/createNft');
    };

    const handleSellNft = (nftId: number, activeAuctionId: number | null) => {
        activeAuctionId
            ? navigate(`/browse/${schoolId}/${activeAuctionId}`)
            : navigate(`/sellNft/${nftId}`);
    };

    const getOwnedNftsData = async () => {
        const response = await axios.get(`/api/account/${accountId}`);
        return response.data.ownedNfts;
    };

    const { data } = useQuery<MinimalNft[]>(
        [API_KEYS.GET_OWNED_NFTS],
        getOwnedNftsData
    );

    return data ? (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            <div className="flex justify-around w-full">
                <span>
                    <h3 className="text-4xl font-bold">Your items</h3>
                    <h4 className="text-xl mt-3">
                        Browse items you created or bought
                    </h4>
                </span>
                <button className="btn btn-primary" onClick={handleCreateNft}>
                    Create an item
                </button>
            </div>
            <section className="flex gap-10 mt-16 flex-wrap justify-center w-full">
                {data.length > 0 ? (
                    data.map(
                        ({
                            nftId,
                            name,
                            description,
                            uri,
                            activeAuctionId,
                        }) => (
                            <div
                                key={nftId}
                                className="max-w-xs min-w-[15rem] bg-gray/5 rounded-xl"
                            >
                                <img
                                    src={getIpfsImage(uri)}
                                    alt="nft"
                                    className="rounded-t-xl h-80 w-80"
                                />
                                <div className="p-5 rounded-b-xl text-center flex flex-col">
                                    <p className="font-bold text-2xl">{name}</p>
                                    <p className="font-light mt-2 h-20">
                                        {description}
                                    </p>
                                    <button
                                        className="btn btn-primary mt-5"
                                        onClick={() =>
                                            handleSellNft(
                                                nftId,
                                                activeAuctionId
                                            )
                                        }
                                    >
                                        {activeAuctionId
                                            ? 'Show auction'
                                            : 'Sell'}
                                    </button>
                                </div>
                            </div>
                        )
                    )
                ) : (
                    <div className="flex flex-col items-center bg-black/20 p-10 rounded-xl">
                        <h3 className="text-3xl font-bold">
                            {"You don't have any items yet"}
                        </h3>
                        <h4 className="text-xl mt-3">
                            Create or buy some to see them here
                        </h4>
                    </div>
                )}
            </section>
        </main>
    ) : (
        <progress className="progress w-56" />
    );
};

export default OwnedNftsPage;
