import { useState, useEffect } from 'react';
import Web3 from 'web3';
import Auctions from '../Auctions/Auctions';
import Nfts from '../Nfts/Nfts';
import { Button, Divider, Spin } from 'antd';
import * as Styled from './App.styles';
import { useAppSelector } from './../hooks/useAppSelector';
import { useAppDispatch } from '../hooks/useAppDispatch';
import { setIsMetaMaskConnected } from '../store/appSlice';

const App = () => {
    const isMetaMaskConnected = useAppSelector(
        (state) => state.app.isMetaMaskConnected
    );
    const dispatch = useAppDispatch();

    const handleConnectWallet = async () => {
        if (window.ethereum) {
            window.web3 = new Web3(window.ethereum);
            try {
                await window.ethereum.enable();
                dispatch(setIsMetaMaskConnected(true));
            } catch (err) {
                console.log(err);
            }
        } else if (window.web3)
            window.web3 = new Web3(window.web3.currentProvider);
        else
            console.log(
                'Non-Ethereum browser detected. You should add MetaMask to your extensions!'
            );
    };

    useEffect(() => {
        const init = async () => {
            console.log('WYKONUJE');
            const accounts = await window.ethereum.request({
                method: 'eth_accounts',
            });
            if (accounts.length) {
                console.log(`You're connected to: ${accounts[0]}`);
                dispatch(setIsMetaMaskConnected(true));
            } else {
                console.log('Metamask is not connected');
            }
        };

        init();
    }, []);

    return (
        <Styled.Container>
            <Button
                onClick={handleConnectWallet}
                disabled={isMetaMaskConnected}
            >
                Connect wallet
            </Button>
            {isMetaMaskConnected ? (
                <>
                    <h3>Enabled</h3>
                    {/* <Auctions />
                    <Divider />
                    <Nfts /> */}
                </>
            ) : (
                <Spin />
            )}
        </Styled.Container>
    );
};

export default App;
