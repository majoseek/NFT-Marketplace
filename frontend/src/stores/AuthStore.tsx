import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { useMarketplaceStore } from './MarketplaceStore';

type AuthStoreState = {
    accountId: number | null;
    name: string | null;
};

type AuthStoreActions = {
    loginUser: (accountId: number, name: string) => void;
    logoutUser: () => void;
    isUserLoggedIn: () => boolean;
};

const initialAuthState: AuthStoreState = {
    accountId: null,
    name: null,
};

export const useAuthStore = create(
    persist<AuthStoreActions & AuthStoreState>(
        (set, get) => ({
            ...initialAuthState,

            loginUser: (accountId: number, name: string) => {
                set({ accountId, name });
            },
            logoutUser: () => {
                useMarketplaceStore.getState().clearChosenSchool();
                set(initialAuthState);
            },
            isUserLoggedIn: () => {
                return get().accountId !== null;
            },
        }),
        {
            name: 'auth-store',
            getStorage: () => localStorage,
        }
    )
);
