import rocketImage from '@/assets/hero-ilustration.svg';
import * as Styled from './LandingPage.styles';
import Button from '@/components/Button';
import Header from '@/components/Header';

const LandingPage = () => {
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
                    <Button type="primary">GET STARTED</Button>
                </Styled.LeftSide>
                <Styled.LandingImg src={rocketImage} alt="" />
            </Styled.SidesWrapper>
        </Styled.LandingPage>
    );
};

export default LandingPage;
