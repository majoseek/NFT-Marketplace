import { useEffect, useState } from 'react';
import Web3 from 'web3';
import Auctions from '../pages/Auctions/Auctions';
import Nfts from '../pages/Nfts/Nfts';
import { useAppSelector } from '../hooks/useAppSelector';
import { useAppDispatch } from '../hooks/useAppDispatch';
import { setHasMetaMaskProvider, setWallets } from '../store/appSlice';
import LandingPage from '../pages/LandingPage';
import detectEthereumProvider from '@metamask/detect-provider';
import LoadingPage from '@/pages/LoadingPage';
import ErrorPage from '@/pages/ErrorPage';
import TopMenu from '@/components/TopMenu';

const App = () => {
    const hasMetaMaskProvider = useAppSelector(
        (state) => state.app.hasMetaMaskProvider
    );
    const wallets = useAppSelector((state) => state.app.wallets);
    const dispatch = useAppDispatch();
    const [isLoadingExtensions, setIsLoadingExtensions] = useState(true);

    useEffect(() => {
        const refreshAccounts = (accounts: unknown[]) => {
            if (accounts.length > 0) dispatch(setWallets(accounts));
            else dispatch(setWallets([]));
        };

        const getProvider = async () => {
            const provider = await detectEthereumProvider({ silent: true });

            if (provider) {
                const accounts = await window.ethereum.request({
                    method: 'eth_accounts',
                });
                refreshAccounts(accounts);
                dispatch(setHasMetaMaskProvider(true));
                window.ethereum.on('accountsChanged', refreshAccounts);
            } else dispatch(setHasMetaMaskProvider(false));
        };

        const extensionTimeout = setTimeout(() => {
            setIsLoadingExtensions(false);
        }, 1000);

        !isLoadingExtensions && getProvider();

        return () => {
            clearTimeout(extensionTimeout);
            window.ethereum?.removeListener('accountsChanged', refreshAccounts);
        };
    }, [isLoadingExtensions]);

    return (
        <>
            {isLoadingExtensions || hasMetaMaskProvider === undefined ? (
                <LoadingPage title="Loading marketplace ..." />
            ) : hasMetaMaskProvider === true &&
              wallets &&
              wallets.length > 0 ? (
                <>
                    <TopMenu />
                    <Auctions />
                    {/* <Auctions />
                    <Divider />
                    <Nfts /> */}
                </>
            ) : hasMetaMaskProvider === true &&
              wallets &&
              wallets.length === 0 ? (
                <LandingPage />
            ) : (
                <ErrorPage title="Non-ethereum browser detected!" />
            )}
        </>
    );
};

export default App;
