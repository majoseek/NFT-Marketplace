import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

export interface AppState {
    hasMetaMaskProvider?: boolean;
    wallets?: string[];
}

const initialState: AppState = {};

export const appSlice = createSlice({
    name: 'counter',
    initialState,
    reducers: {
        setHasMetaMaskProvider: (
            state,
            action: PayloadAction<AppState['hasMetaMaskProvider']>
        ) => {
            state.hasMetaMaskProvider = action.payload;
        },

        setWallets: (state, action: PayloadAction<AppState['wallets']>) => {
            state.wallets = action.payload;
        },
    },
});

export const { setHasMetaMaskProvider, setWallets } = appSlice.actions;

export default appSlice.reducer;
