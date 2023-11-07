import { Route, Routes } from 'react-router-dom';
import Header from '../components/Header';
import CreateNftPage from '../pages/CreateNftPage';
import LandingPage from '../pages/LandingPage';
import NftsPage from '../pages/NftsPage';
import OwnedNftsPage from '../pages/OwnedNftsPage';
import AuctionPage from '../pages/AuctionPage';
import CreateAuctionPage from '../pages/CreateAuctionPage';
import { setHasMetaMaskProvider, setWallets } from '@/store/appSlice';
import detectEthereumProvider from '@metamask/detect-provider';
import { useEffect, useState } from 'react';
import { useAppDispatch } from '@/hooks/useAppDispatch';
import ProtectedRoute from '@/components/ProtectedRoute';

const App = () => {
    const dispatch = useAppDispatch();

    useEffect(() => {
        const refreshAccounts = (accounts: unknown[]) => {
            console.log('acc[]', accounts);
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

        getProvider();

        return () => {
            window.ethereum?.removeListener('accountsChanged', refreshAccounts);
        };
    }, []);

    return (
        <div className="relative min-h-screen">
            <Header />
            <ProtectedRoute>
                <Routes>
                    <Route element={<LandingPage />} path="/" />
                    <Route element={<OwnedNftsPage />} path="/ownedNfts" />
                    <Route element={<CreateNftPage />} path="/createNft" />
                    <Route element={<NftsPage />} path="/browse/:schoolId" />
                    <Route
                        element={<AuctionPage />}
                        path="/browse/:schoolId/:auctionId"
                    />
                    <Route
                        element={<CreateAuctionPage />}
                        path="/sellNft/:nftId"
                    />
                </Routes>
            </ProtectedRoute>
        </div>
    );
};

export default App;
