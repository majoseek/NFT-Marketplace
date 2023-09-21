import { Typography } from 'antd';
import styled from 'styled-components';
import bgImage from '@/assets/Background.svg';

export const LandingPage = styled.section`
    width: 100%;
    min-height: 100vh;
    background-image: url(${bgImage});
    background-repeat: no-repeat;
    background-color: currentColor;
`;

export const SidesWrapper = styled.div`
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
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
        color: ${({ theme }) => theme.textColors.primary};
        max-width: 570px;
        font-family: 'Chillax', sans-serif;
        text-align: center;
    }
`;

export const LandingDescription = styled(Typography.Text)`
    color: ${({ theme }) => theme.textColors.primary};
    font-family: 'Chillax', sans-serif;
    text-align: center;
    font-size: 1.1rem;
`;
