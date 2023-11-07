import RocketIcon from '@/assets/icons/rocketIcon.svg';
import LandingImage from '@/assets/images/landingImage.svg';
import userAvatar from '@/assets/images/userAvatar.png';
import { useAppDispatch } from '@/hooks/useAppDispatch';
import { setWallets } from '@/store/appSlice';
import { useState } from 'react';

const LandingPage = () => {
    const dispatch = useAppDispatch();
    const [connectWalletMessage, setConnectWalletMesage] = useState<{
        type: 'INFO' | 'ERROR';
        content: string;
    }>();

    const handleConnectWallet = async () => {
        if (!window.ethereum && !window.web3) {
            setConnectWalletMesage({
                type: 'ERROR',
                content:
                    'Non-Ethereum browser detected. You should add MetaMask to your extensions!',
            });
            return;
        }

        try {
            const accounts = await window.ethereum.request({
                method: 'eth_requestAccounts',
            });
            dispatch(setWallets(accounts));
        } catch {
            setConnectWalletMesage({
                type: 'INFO',
                content: 'Connecting wallet, please check MetaMask extension!',
            });
        }
    };

    return (
        <>
            {connectWalletMessage && (
                <div className="toast toast-top toast-center">
                    <div
                        className={`alert ${
                            connectWalletMessage.type === 'ERROR'
                                ? 'alert-error'
                                : 'alert-info'
                        }`}
                    >
                        <span className="text-white">
                            {connectWalletMessage.content}
                        </span>
                    </div>
                </div>
            )}
            <main className="p-20 flex gap-7 justify-center items-center">
                <>
                    <section className="flex flex-col">
                        <span className="font-semibold text-5xl mb-4 leading-tight">
                            Discover
                            <br />
                            Digital Work &
                            <br />
                            Collect Items
                        </span>
                        <span className="text-base max-w-md">
                            {`Experience the future of ownership with our NFT Marketplace! Dive into a world of digital art, collectibles, and endless possibilities.`}
                        </span>
                        <button
                            onClick={handleConnectWallet}
                            className="btn btn-primary mt-24 w-fit text-white"
                        >
                            <img
                                src={RocketIcon}
                                className="mr-3"
                                alt="rocket"
                            />
                            Connect wallet
                        </button>
                        <div className="flex justify-between mt-8 max-w-sm">
                            <div className="flex flex-col ">
                                <span className="font-semibold text-2xl text-center">
                                    672189+
                                </span>
                                <span className="text-lg text-center">
                                    Users
                                </span>
                            </div>
                            <div className="flex flex-col ">
                                <span className="font-semibold text-2xl text-center">
                                    52176521+
                                </span>
                                <span className="text-lg text-center">
                                    NFTs
                                </span>
                            </div>
                            <div className="flex flex-col ">
                                <span className="font-semibold text-2xl text-center">
                                    15120+
                                </span>
                                <span className="text-lg text-center">
                                    Auctions
                                </span>
                            </div>
                        </div>
                    </section>
                    <section className="hidden md:block">
                        <img src={LandingImage} alt="landing" />
                        <div className="bg-primary p-5 rounded-b-xl">
                            <span className="font-medium text-2xl">
                                Space Walking
                            </span>
                            <span className="flex mt-3 gap-3 leading-xs items-center">
                                <img src={userAvatar} alt="user" />
                                Bartek Biga
                            </span>
                        </div>
                    </section>
                </>
            </main>
        </>
    );
};
export default LandingPage;
