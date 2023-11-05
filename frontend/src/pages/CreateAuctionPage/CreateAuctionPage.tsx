import { useQuery } from '@tanstack/react-query';
import axios from 'axios';
import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { API_KEYS } from '../../api/API_KEYS';

type Nft = {
    nftId: number;
    name: string;
    description: string;
    uri: string;
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

const CreateAuctionPage = () => {
    const { nftId } = useParams<{ nftId: string }>();
    const [selectedDuration, setSelectedDuration] = useState(0);
    const durations = [1, 5, 15, 30, 45, 60];
    const { data: nftResponse } = useQuery<Nft>([API_KEYS.GET_NFT], () =>
        axios.get(`/api/nft/${nftId}`).then((response) => response.data)
    );

    const handleCreateAuctionClick = () => {};

    return nftResponse ? (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            <div className="flex justify-around w-full">
                <span>
                    <h3 className="text-4xl font-bold">
                        {`Create auction for "${nftResponse.name}" NFT`}
                    </h3>
                    <h4 className="text-xl mt-3">
                        Just type in auction duration
                    </h4>
                </span>
            </div>
            <section className="flex gap-10 mt-16 flex-wrap justify-center w-full">
                <div className="flex flex-col gap-6 justify-center items-center">
                    <div className="max-w-xs min-w-[15rem] bg-gray/5 rounded-xl">
                        <img
                            src=""
                            alt="nft"
                            className="rounded-t-xl h-80 w-80"
                        />
                        <div className="p-5 rounded-b-xl text-center">
                            <p className="font-bold text-2xl">
                                {nftResponse.name}
                            </p>
                            <p className="font-light mt-2">
                                {nftResponse.description}
                            </p>
                        </div>
                    </div>
                    <div className="flex gap-3 justify-center items-center">
                        <div className="dropdown">
                            <span className="btn m-1">
                                Select auction duration
                            </span>
                            <ul className="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52 flex-nowrap max-h-48 overflow-y-scroll">
                                {durations.map((duration) => (
                                    <li key={duration}>
                                        <label className="label cursor-pointer">
                                            <span className="label-text">
                                                {duration} minutes
                                            </span>
                                            <input
                                                type="checkbox"
                                                onChange={() =>
                                                    selectedDuration ===
                                                    duration
                                                        ? setSelectedDuration(0)
                                                        : setSelectedDuration(
                                                              duration
                                                          )
                                                }
                                                checked={
                                                    selectedDuration ===
                                                    duration
                                                }
                                                className="checkbox checkbox-primary"
                                            />
                                        </label>
                                    </li>
                                ))}
                            </ul>
                        </div>
                        <button
                            className="btn btn-primary"
                            disabled={selectedDuration === 0}
                            onClick={handleCreateAuctionClick}
                        >
                            Create auction
                        </button>
                    </div>
                </div>
            </section>
        </main>
    ) : (
        <progress className="progress w-56" />
    );
};

export default CreateAuctionPage;
