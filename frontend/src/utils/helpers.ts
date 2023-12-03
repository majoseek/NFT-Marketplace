export const convertEthToWei = (priceInEth: number) => {
    const WEI_PER_ETH = 1e18;

    if (priceInEth < 0.1) return (priceInEth * WEI_PER_ETH).toString() + ' WEI';
    else return priceInEth.toString() + ' ETH';
};
