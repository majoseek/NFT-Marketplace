import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

export interface AppState {
    isMetaMaskConnected: boolean;
}

const initialState: AppState = {
    isMetaMaskConnected: false,
};

export const appSlice = createSlice({
    name: 'counter',
    initialState,
    reducers: {
        setIsMetaMaskConnected: (state, action: PayloadAction<boolean>) => {
            state.isMetaMaskConnected = action.payload;
        },
    },
});

export const { setIsMetaMaskConnected } = appSlice.actions;

export default appSlice.reducer;
