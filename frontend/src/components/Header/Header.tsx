import MarketIcon from '../../assets/icons/marketIcon.svg';

const Header = () => {
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
                {true && (
                    <div className="flex gap-14 items-center">
                        <div className="flex flex-row items-center gap-2">
                            <button
                                className="btn btn-secondary"
                                onClick={() => {}}
                            >
                                MY ITEMS
                            </button>
                        </div>
                    </div>
                )}
            </header>
        </>
    );
};
export default Header;
