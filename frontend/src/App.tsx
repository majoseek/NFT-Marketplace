import { useEffect, useState } from "react";
import Web3 from "web3";
import Auctions from "./Auctions/Auctions";
import Nfts from "./Nfts/Nfts";
import { Divider, Spin } from "antd";

const App = () => {
  const [web3Enabled, setWeb3Enabled] = useState(false);

  useEffect(() => {
    const init = async () => {
      if (window.ethereum) {
        window.web3 = new Web3(window.ethereum);
        await window.ethereum.enable();
        setWeb3Enabled(true);
      } else if (window.web3) {
        window.web3 = new Web3(window.web3.currentProvider);
      } else {
        console.log(
          "Non-Ethereum browser detected. You should consider trying MetaMask!"
        );
        return;
      }
    };

    init();
  }, []);

  return (
    <div>
      {web3Enabled ? (
        <>
          <Auctions />
          <Divider />
          <Nfts />
        </>
      ) : (
        <Spin />
      )}
    </div>
  );
};

export default App;
