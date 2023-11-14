import { useAppSelector } from '@/hooks/useAppSelector';
import MarketIcon from '../../assets/icons/marketIcon.svg';
import { useNavigate } from 'react-router-dom';

const Header = () => {
    const wallets = useAppSelector((state) => state.app.wallets);
    const navigate = useNavigate();

    const handleMyItemsClick = () => {
        navigate('/ownedNfts');
    };

    const handleAuctionsClick = () => {
        navigate('/');
    };

    const handleLogoClick = () => {
        navigate('/');
    };

    return (
        <>
            <header className="flex justify-between px-20 py-9">
                <div className="flex flex-row gap-1 items-center">
                    <div className="flex flex-col items-center">
                        <div
                            className="font-bold flex gap-2 items-center text-xl font-mono cursor-pointer"
                            onClick={handleLogoClick}
                        >
                            <img src={MarketIcon} alt="nft-marketplace" />
                            NFT Marketplace
                        </div>
                    </div>
                </div>
                {wallets && wallets.length > 0 && (
                    <div className="flex gap-14 items-center">
                        <div className="flex flex-row items-center gap-2">
                            <button
                                className="btn btn-primary text-white"
                                onClick={handleAuctionsClick}
                            >
                                Auctions
                            </button>
                            <button
                                className="btn btn-primary text-white"
                                onClick={handleMyItemsClick}
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
