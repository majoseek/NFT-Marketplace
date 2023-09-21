import rocketImage from '@/assets/hero-ilustration.svg';
import * as Styled from './LandingPage.styles';
import Button from '@/components/Button';
import Header from '@/components/Header';
import { useAppDispatch } from '@/hooks/useAppDispatch';
import { setWallets } from '@/store/appSlice';
import { message } from 'antd';

const LandingPage = () => {
    const dispatch = useAppDispatch();

    const handleConnectWallet = async () => {
        if (!window.ethereum && !window.web3) {
            message.error(
                'Non-Ethereum browser detected. You should add MetaMask to your extensions!'
            );
            return;
        }

        try {
            const accounts = await window.ethereum.request({
                method: 'eth_requestAccounts',
            });
            dispatch(setWallets(accounts));
        } catch {
            message.info('Connecting wallet, please check MetaMask extension!');
        }
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
