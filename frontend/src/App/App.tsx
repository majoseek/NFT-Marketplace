import { useEffect } from 'react';
import bgImage from '../assets/bgStars.png';
import Web3 from 'web3';
import Auctions from '../pages/Auctions/Auctions';
import Nfts from '../pages/Nfts/Nfts';
import { Spin } from 'antd';
import { useAppSelector } from '../hooks/useAppSelector';
import { useAppDispatch } from '../hooks/useAppDispatch';
import { setIsMetaMaskConnected } from '../store/appSlice';
import LandingPage from '../pages/LandingPage';
import LoadingPage from '@/pages/LoadingPage';

const App = () => {
    const isMetaMaskConnected = useAppSelector(
        (state) => state.app.isMetaMaskConnected
    );
    const dispatch = useAppDispatch();

    useEffect(() => {
        const init = async () => {
            const accounts = await window.ethereum.request({
                method: 'eth_accounts',
            });
            if (accounts.length) dispatch(setIsMetaMaskConnected(true));
            else dispatch(setIsMetaMaskConnected(false));
        };

        init();
    }, []);

    return (
        <>
            {isMetaMaskConnected === true ? (
                <>
                    <h3>Enabled</h3>
                    {/* <Auctions />
                    <Divider />
                    <Nfts /> */}
                </>
            ) : isMetaMaskConnected === false ? (
                <LoadingPage title="Loading MetaMask..." />
            ) : (
                <Spin />
            )}
        </>
    );
};

export default App;
