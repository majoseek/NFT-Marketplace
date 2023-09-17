import rocketImage from '@/assets/hero-ilustration.svg';
import * as Styled from './LandingPage.styles';
import { Button } from 'antd';

const LandingPage = () => {
    return (
        <Styled.LandingPage>
            <Styled.LeftSide>
                <Styled.LandingTitle>
                    THE FIRST NFT for Transparency and Community-Driven
                </Styled.LandingTitle>
                <Button>Get started</Button>
            </Styled.LeftSide>
            <img src={rocketImage} alt="" />
        </Styled.LandingPage>
    );
};

export default LandingPage;
