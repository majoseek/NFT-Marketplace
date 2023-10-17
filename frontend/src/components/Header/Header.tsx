import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { API_KEYS } from "../../api/API_KEYS";
import { ReactComponent as MarketIcon } from "../../assets/icons/marketIcon.svg";
import { ReactComponent as UserIcon } from "../../assets/icons/userIcon.svg";
import { ReactComponent as WalletIcon } from "../../assets/icons/wallet.svg";
import { ReactComponent as ArrowUpIcon } from "../../assets/icons/arrowUp.svg";
import { useWallet } from "../../hooks/useWallet";
import { useAuthStore } from "../../stores/AuthStore";
import { useMarketplaceStore } from "../../stores/MarketplaceStore";
import { useState } from "react";
import { useAccountUpdates } from "../../hooks/useAccountUpdates";

const Header = () => {
  const navigate = useNavigate();
  const { isUserLoggedIn, name, logoutUser } = useAuthStore();
  const { schoolId } = useMarketplaceStore();
  const [fundsToAdd, setFundsToAdd] = useState("");
  const { data: schoolsResponse, isLoading } = useQuery(
    [API_KEYS.GET_SCHOOL_INFO],
    () => axios.get(`/api/school/${schoolId}`).then((response) => response),
    { enabled: !!schoolId }
  );
  const { mutateAsync: mutateAddBalance } = useMutation(
    [API_KEYS.ADD_BALANCE],
    () => axios.put("/api/account/funds", { balanceToAdd: Number(fundsToAdd) }),
    {
      onSuccess: () => {
        refetchWallet();
        setFundsToAdd("");
      },
    }
  );
  const { mutateAsync: mutateAcceptAuction } = useMutation(
    [API_KEYS.ACCEPT_AUCTION],
    (auctionId: number) => axios.put(`/api/auction/${auctionId}/confirm`),
    {
      onSuccess: () => {
        let queryClient = useQueryClient();
        refetch();
        queryClient.invalidateQueries({ queryKey: [API_KEYS.GET_OWNED_NFTS] });
      },
    }
  );
  const { mutateAsync: mutateRejectAuction } = useMutation(
    [API_KEYS.REJECT_AUCTION],
    (auctionId: number) => axios.put(`/api/auction/${auctionId}/reject`),
    {
      onSuccess: () => {
        let queryClient = useQueryClient();
        refetch();
        queryClient.invalidateQueries({ queryKey: [API_KEYS.GET_OWNED_NFTS] });
      },
    }
  );

  const { data: walletResponse, refetch: refetchWallet, isLoading: walletLoading } = useWallet();

  const { data: accountResponse, refetch } = useAccountUpdates();

  const canAddFunds = /^\d+$/.test(fundsToAdd) && fundsToAdd !== "";

  const handleAddFunds = () => {
    fundsToAdd !== "" && mutateAddBalance();
  };

  const handleLogoClick = () => navigate("/");

  const handleSignUpClick = () => navigate("/register");

  const handleLoginClick = () => navigate("/login");

  const handleSchoolChangeClick = () => navigate("/school");

  const handleNftsClick = () => navigate("/ownedNfts");

  const handleBrowseClick = () => navigate("/browse");

  const handleLogoutClick = () => logoutUser();

  const showAuctionFinished =
    accountResponse && accountResponse?.ownedAuctionsUpdates.length > 0;

  return (
    <>
      <label
        className={`${
          showAuctionFinished ? "flex" : "hidden"
        } justify-center mx-20 -mb-4 mt-4 px-4 py-4 bg-red-600/25 rounded-lg relative cursor-pointer`}
        htmlFor="auction-confirm-modal"
      >
        Your item has been sold! Click here to confirm the transaction.
        <div
          className="btn btn-outline btn-error btn-sm absolute right-4 top-3"
          onClick={() => {}}
        >
          CONFIRM
        </div>
      </label>
      <header className="flex justify-between px-20 py-9">
        <div className="flex flex-row gap-1 items-center">
          <div className="flex flex-col items-center">
            <div
              className="font-bold flex gap-2 items-center text-xl font-mono cursor-pointer"
              onClick={handleLogoClick}
            >
              <MarketIcon />
              NFT Marketplace
            </div>
            {schoolId && (
              <div className="flex flex-row items-center gap-2">
                <span>{schoolsResponse?.data.name}</span>
              </div>
            )}
          </div>
          <div className="divider divider-horizontal"></div>
          <div className="btn btn-primary" onClick={handleBrowseClick}>
            Browse auctions
          </div>
          {schoolId && (
            <span
              className="btn btn-outline text-base-content/75 ml-2"
              onClick={handleSchoolChangeClick}
            >
              Change school
            </span>
          )}
        </div>

        <div className="flex gap-14 items-center">
          <div className="flex flex-row items-center gap-2">
            {isUserLoggedIn() ? (
              <>
                <span className="flex flex-row items-center text-white font-semibold text-lg p-3 px-4 h-12 bg-black/20 rounded-lg">
                  <UserIcon className="mr-3 w-4" />
                  {name}
                </span>
                <label
                  htmlFor="wallet-modal"
                  className="flex flex-row items-center text-white font-semibold text-lg p-3 px-4 h-12 bg-black/20 rounded-lg cursor-pointer hover:bg-black/10"
                >
                  <WalletIcon className="mr-3 w-4" />
                  {(walletResponse && !walletLoading ? walletResponse.data.balance + "$" : "Wallet")}
                </label>
                <button
                  className="btn btn-secondary"
                  onClick={() => handleNftsClick()}
                >
                  MY ITEMS
                </button>
                <button
                  className="btn btn-primary"
                  onClick={() => handleLogoutClick()}
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={handleLoginClick}
                  className="btn btn-secondary w-fit"
                >
                  Login
                </button>

                <button
                  onClick={handleSignUpClick}
                  className="btn btn-primary w-fit"
                >
                  Sign up
                </button>
              </>
            )}
          </div>
        </div>
        {walletResponse && (
          <>
            <input type="checkbox" id="wallet-modal" className="modal-toggle" />
            <div className="modal">
              <div className="modal-box relative">
                <label
                  htmlFor="wallet-modal"
                  className="btn btn-sm btn-circle absolute right-2 top-2"
                >
                  ✕
                </label>
                <h3 className="font-bold text-lg text-center">Your wallet</h3>
                <p className="py-4 text-center">
                  Current balance: <b>{walletResponse.data.balance}</b> $
                </p>
                <div className="flex gap-4 items-center justify-around mt-3">
                  <div className="flex">
                    <span className="inline-flex items-center px-3 text-sm text-gray-900 bg-gray-200 border rounded-l-md border-secondary">
                      $
                    </span>
                    <input
                      type="text"
                      className="input rounded-none rounded-r-lg bg-gray-50 border border-primary border-solid text-gray-900 block flex-1 min-w-0 w-full text-sm p-2.5"
                      placeholder="Type amount of funds"
                      onChange={(e) => {
                        setFundsToAdd(e.target.value);
                      }}
                      value={fundsToAdd}
                    />
                  </div>
                  <button
                    onClick={handleAddFunds}
                    className="btn btn-primary w-fit"
                    disabled={!canAddFunds}
                  >
                    <ArrowUpIcon className="mr-3 w-6 h-6" />
                    Add funds
                  </button>
                </div>
              </div>
            </div>
          </>
        )}
      </header>
      <input
        type="checkbox"
        id="auction-confirm-modal"
        className="modal-toggle"
      />
      <label htmlFor="auction-confirm-modal" className="modal cursor-pointer">
        <label className="modal-box relative" htmlFor="">
          {accountResponse &&
          accountResponse?.ownedAuctionsUpdates.length > 0 ? (
            <>
              <h3 className="text-lg font-bold">
                Your auctions which needs to be confirmed:
              </h3>
              {accountResponse.ownedAuctionsUpdates.map((auction) => (
                <div className="mt-14 flex justify-between items-center">
                  <p>{auction.nftName} has been won!</p>
                  <button
                    className="btn btn-success"
                    onClick={() => mutateAcceptAuction(auction.auctionId)}
                  >
                    Accept
                  </button>
                  <button
                    className="btn btn-error"
                    onClick={() => mutateRejectAuction(auction.auctionId)}
                  >
                    Reject
                  </button>
                </div>
              ))}
            </>
          ) : (
            <h3>Nothing to confirm!</h3>
          )}
        </label>
      </label>
    </>
  );
};
export default Header;
