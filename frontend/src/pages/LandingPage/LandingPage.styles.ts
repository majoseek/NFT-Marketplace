import { Typography } from 'antd';
import styled from 'styled-components';

export const LandingPage = styled.section`
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-around;
    align-items: center;

    img {
        width: 512px;
    }
`;

export const LeftSide = styled.div``;

export const LandingTitle = styled(Typography.Title)`
    &&& {
        color: #9f7fdb;
        max-width: 570px;
        font-family: 'Chillax', sans-serif;
    }
`;
