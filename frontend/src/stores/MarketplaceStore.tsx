import { create } from "zustand";
import { persist } from "zustand/middleware";

type MarketplaceStoreState = {
  schoolId: number | null;
};

type MarketplaceStoreActions = {
  isSchoolChosen: () => boolean;
  setChosenSchool: (schoolId: number) => void;
  clearChosenSchool: () => void;
};

const initialMarketplaceState: MarketplaceStoreState = {
  schoolId: null,
};

export const useMarketplaceStore = create(persist<MarketplaceStoreActions & MarketplaceStoreState>(((set, get) => ({
  ...initialMarketplaceState,
  
  setChosenSchool: (schoolId: number) => {
    set({ schoolId: schoolId });
  },

  clearChosenSchool: () => {
    set({ schoolId: null });
  },

  isSchoolChosen: () => {
    return get().schoolId !== null;
  }
})
), {
  name: "marketplace-store",
  getStorage: () => localStorage,
}));