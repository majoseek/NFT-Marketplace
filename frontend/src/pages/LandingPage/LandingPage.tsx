import rocketImage from '@/assets/hero-ilustration.svg';
import * as Styled from './LandingPage.styles';
import Button from '@/components/Button';
import Header from '@/components/Header';
import { useAppDispatch } from '@/hooks/useAppDispatch';
import { setIsMetaMaskConnected } from '@/store/appSlice';
import Web3 from 'web3';
import { message } from 'antd';

const LandingPage = () => {
    const dispatch = useAppDispatch();

    const handleConnectWallet = async () => {
        if (window.ethereum) {
            window.web3 = new Web3(window.ethereum);
            try {
                await window.ethereum.enable();
                dispatch(setIsMetaMaskConnected(true));
            } catch (err) {
                console.log(err);
                message.info(`We've sent you notification on MetaMask!`);
            }
        } else if (window.web3)
            window.web3 = new Web3(window.web3.currentProvider);
        else
            message.error(
                'Non-Ethereum browser detected. You should add MetaMask to your extensions!'
            );
    };

    return (
        <Styled.LandingPage>
            <Header />
            <Styled.SidesWrapper>
                <Styled.LeftSide>
                    <Styled.LandingTitle>
                        Embrace the NFT Revolution
                    </Styled.LandingTitle>
                    <Styled.LandingDescription>
                        Connect your wallet and explore a world of unique
                        digital assets
                    </Styled.LandingDescription>
                    <Button type="primary" onClick={handleConnectWallet}>
                        GET STARTED
                    </Button>
                </Styled.LeftSide>
                <Styled.LandingImg src={rocketImage} alt="" />
            </Styled.SidesWrapper>
        </Styled.LandingPage>
    );
};

export default LandingPage;
