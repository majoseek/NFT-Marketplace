import { useNavigate } from "react-router-dom";
import { ReactComponent as MarketIcon } from "../../assets/icons/marketIcon.svg";

const Footer = () => {
  const navigate = useNavigate();

  const handleMarketplaceClick = () => navigate("/browse");

  const handleOwnedNftsClick = () => navigate("/ownedNfts");

  return (
    <footer className="px-20 flex gap-5 absolute bottom-14 w-full justify-center">
      <div className="flex flex-col gap-6 w-80">
        <span className="flex font-bold justify-center gap-2 items-center font-mono">
          <MarketIcon />
          <h3>NFT Marketplace</h3>
        </span>
        <span className="text-gray text-center">
          NFT Marketplace for schools created for Warsaw University of
          Technology
        </span>
      </div>
      <div className="flex flex-col items-center gap-3 w-80">
        <span className="font-bold font-mono text-xl">Explore</span>
        <span
          className="text-gray cursor-pointer hover:text-white"
          onClick={handleMarketplaceClick}
        >
          Marketplace
        </span>
        <span
          className="text-gray cursor-pointer hover:text-white"
          onClick={handleOwnedNftsClick}
        >
          Your items
        </span>
      </div>
    </footer>
  );
};

export default Footer;
