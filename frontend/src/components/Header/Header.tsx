import MarketIcon from '../../assets/icons/marketIcon.svg';
import UserIcon from '../../assets/icons/userIcon.svg';
import WalletIcon from '../../assets/icons/wallet.svg';

const Header = () => {
    const showAuctionFinished = true;

    return (
        <>
            <label
                className={`${
                    showAuctionFinished ? 'flex' : 'hidden'
                } justify-center mx-20 -mb-4 mt-4 px-4 py-4 bg-red-600/25 rounded-lg relative cursor-pointer`}
                htmlFor="auction-confirm-modal"
            >
                Your item has been sold! Click here to confirm the transaction.
                <div className="btn btn-outline btn-error btn-sm absolute right-4 top-3">
                    CONFIRM
                </div>
            </label>
            <header className="flex justify-between px-20 py-9">
                <div className="flex flex-row gap-1 items-center">
                    <div className="flex flex-col items-center">
                        <div
                            className="font-bold flex gap-2 items-center text-xl font-mono cursor-pointer"
                            onClick={() => {}}
                        >
                            <img src={MarketIcon} alt="nft-marketplace" />
                            NFT Marketplace
                        </div>
                    </div>
                </div>

                <div className="flex gap-14 items-center">
                    <div className="flex flex-row items-center gap-2">
                        {true ? (
                            <>
                                <span className="flex flex-row items-center text-white font-semibold text-lg p-3 px-4 h-12 bg-black/20 rounded-lg">
                                    <img
                                        src={UserIcon}
                                        className="mr-3 w-4"
                                        alt="user-icon"
                                    />
                                    SomeUserName
                                </span>
                                <label
                                    htmlFor="wallet-modal"
                                    className="flex flex-row items-center text-white font-semibold text-lg p-3 px-4 h-12 bg-black/20 rounded-lg cursor-pointer hover:bg-black/10"
                                >
                                    <img
                                        src={WalletIcon}
                                        className="mr-3 w-4"
                                        alt="wallet-icon"
                                    />
                                </label>
                                <button
                                    className="btn btn-secondary"
                                    onClick={() => {}}
                                >
                                    MY ITEMS
                                </button>
                                <button
                                    className="btn btn-primary"
                                    onClick={() => {}}
                                >
                                    Logout
                                </button>
                            </>
                        ) : null}
                    </div>
                </div>
            </header>
            <input
                type="checkbox"
                id="auction-confirm-modal"
                className="modal-toggle"
            />
            <label
                htmlFor="auction-confirm-modal"
                className="modal cursor-pointer"
            >
                <label className="modal-box relative" htmlFor="">
                    {true ? (
                        <>
                            <h3 className="text-lg font-bold">
                                Your auctions which needs to be confirmed:
                            </h3>
                            {[].map((auction) => (
                                <div
                                    className="mt-14 flex justify-between items-center"
                                    key={1}
                                >
                                    <p>NftName has been won!</p>
                                    <button
                                        className="btn btn-success"
                                        onClick={() => {}}
                                    >
                                        Accept
                                    </button>
                                    <button
                                        className="btn btn-error"
                                        onClick={() => {}}
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
