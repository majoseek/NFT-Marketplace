/// <reference types="react-scripts" />
import { ExternalProvider } from '@ethersproject/providers';
import Web3 from 'web3';

declare global {
    interface Window {
        ethereum?: ExternalProvider;
        web3: Web3;
    }
}
