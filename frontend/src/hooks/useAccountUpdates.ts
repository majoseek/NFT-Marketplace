import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import { API_KEYS } from "../api/API_KEYS";
import { useAuthStore } from "../stores/AuthStore";

type OwnedAuctionUpdates = {
  auctionId: number;
  nftId: number;
  nftName: string;
  finalPrice?: number;
};

export const useAccountUpdates = () => {
  const { isUserLoggedIn } = useAuthStore();

  return useQuery<{ ownedAuctionsUpdates: OwnedAuctionUpdates[] }>(
    [API_KEYS.GET_ACCOUNT_UPDATES],
    () =>
      axios.get("/api/account/me/updates").then((response) => response.data),
    { 
      enabled: isUserLoggedIn(),
      refetchInterval: 5000,
    }
  );
};
