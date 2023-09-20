import { Typography } from 'antd';
import styled from 'styled-components';
import bgImage from '@/assets/Background.svg';

export const LandingPage = styled.section`
    width: 100%;
    height: 100%;
    background-image: url(${bgImage});
    background-repeat: no-repeat;
    background-color: currentColor;
`;

export const SidesWrapper = styled.div`
    display: flex;
    justify-content: space-around;
`;

export const LandingImg = styled.img`
    width: 512px;
    height: 512px;
    align-self: center;
`;

export const LeftSide = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    margin: 148px 32px;
    gap: 16px;
`;

export const LandingTitle = styled(Typography.Title)`
    &&& {
        color: #e9e1fd;
        max-width: 570px;
        font-family: 'Chillax', sans-serif;
        text-align: center;
    }
`;

export const LandingDescription = styled(Typography.Text)`
    color: #e9e1fd;
    font-family: 'Chillax', sans-serif;
    text-align: center;
    font-size: 1.1rem;
`;
