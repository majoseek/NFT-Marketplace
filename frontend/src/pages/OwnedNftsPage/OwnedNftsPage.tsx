import { API_KEYS } from '@/api';
import LoadingElement from '@/components/LoadingElement';
import { useAppSelector } from '@/hooks/useAppSelector';
import { Nft } from '@/types/api/auctions';
import axios from 'axios';
import { useQuery } from 'react-query';
import { useNavigate } from 'react-router-dom';

const OwnedNftsPage = () => {
    const navigate = useNavigate();
    const wallets = useAppSelector((state) => state.app.wallets);
    const { data: ownedNfts } = useQuery(
        API_KEYS.NFTS,
        () => {
            if (wallets && wallets.length > 0)
                return axios
                    .get<Nft[]>(`/api/nft/owner/${wallets[0]}`)
                    .then((res) => res.data);
        },
        { enabled: wallets && wallets.length > 0 }
    );

    const handleCreateNft = () => {
        navigate('/createNft');
    };

    const handleSellNft = (nftId: number) => {
        console.log(nftId);
    };

    return (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            {ownedNfts ? (
                <>
                    <div className="flex justify-around w-full">
                        <span>
                            <h3 className="text-4xl font-bold">Your items</h3>
                            <h4 className="text-xl mt-3">
                                Browse items you created or bought
                            </h4>
                        </span>
                        <button
                            className="btn btn-primary text-white"
                            onClick={handleCreateNft}
                        >
                            Create an item
                        </button>
                    </div>
                    <section className="flex gap-10 mt-16 flex-wrap justify-center w-full">
                        {ownedNfts.length > 0 ? (
                            ownedNfts.map(
                                ({
                                    contractAddress,
                                    tokenId,
                                    name,
                                    description,
                                    ownerAddress,
                                    type,
                                    url,
                                }) => (
                                    <div
                                        key={tokenId}
                                        className="max-w-sm min-w-[15rem] bg-gray/5 rounded-xl"
                                    >
                                        <img
                                            src={url}
                                            alt="nft"
                                            className="rounded-t-xl w-full"
                                        />
                                        <div className="p-5 rounded-b-xl text-center flex flex-col">
                                            <p className="font-bold text-2xl">
                                                {name}
                                            </p>
                                            <p className="font-light mt-4">
                                                {description}
                                            </p>
                                            <p className="font-light">
                                                Token address: {tokenId}
                                            </p>
                                            <button
                                                className="btn btn-primary mt-5 text-white"
                                                onClick={() =>
                                                    handleSellNft(tokenId)
                                                }
                                            >
                                                {false
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
                </>
            ) : (
                <LoadingElement
                    title="Loading owned tokens..."
                    className="w-full"
                />
            )}
        </main>
    );
};

export default OwnedNftsPage;
