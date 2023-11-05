import MarketIcon from '../../assets/icons/marketIcon.svg';
import UserIcon from '../../assets/icons/userIcon.svg';
import WalletIcon from '../../assets/icons/wallet.svg';

const Header = () => {
    const showAuctionFinished = true;

    return (
        <>
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
                        {showAuctionFinished ? (
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
        </>
    );
};
export default Header;
