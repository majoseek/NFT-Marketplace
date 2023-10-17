import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import { API_KEYS } from "../api/API_KEYS";
import { useAuthStore } from "../stores/AuthStore";

export const useWallet = () => {
  const { isUserLoggedIn } = useAuthStore();
  return useQuery(
    [API_KEYS.GET_WALLET],
    () => axios.get("/api/account/wallet").then((response) => response),
    { enabled: isUserLoggedIn() }
  );
};
