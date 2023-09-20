import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';

export interface AppState {
    isMetaMaskConnected: boolean | undefined;
}

const initialState: AppState = {
    isMetaMaskConnected: undefined,
};

export const appSlice = createSlice({
    name: 'counter',
    initialState,
    reducers: {
        setIsMetaMaskConnected: (
            state,
            action: PayloadAction<AppState['isMetaMaskConnected']>
        ) => {
            state.isMetaMaskConnected = action.payload;
        },
    },
});

export const { setIsMetaMaskConnected } = appSlice.actions;

export default appSlice.reducer;
