import rocketImage from '@/assets/hero-ilustration.svg';
import * as Styled from './LandingPage.styles';
import Button from '@/components/Button';

const LandingPage = () => {
    return (
        <Styled.LandingPage>
            <Styled.LeftSide>
                <Styled.LandingTitle>
                    Embrace the NFT Revolution
                </Styled.LandingTitle>
                <Button type="primary">GET STARTED</Button>
            </Styled.LeftSide>
            <img src={rocketImage} alt="" />
        </Styled.LandingPage>
    );
};

export default LandingPage;
