import { LoadingOutlined } from '@ant-design/icons';
import { Typography } from 'antd';
import styled from 'styled-components';
import bgImage from '@/assets/bgStars.png';

export const LoadingSpin = styled(LoadingOutlined)`
    &&& {
        font-size: 64px;
    }
`;

export const Wrapper = styled.div`
    height: 100vh;
    display: flex;
    flex-direction: column;
    gap: 32px;
    background-image: url(${bgImage});
    background-repeat: no-repeat;
    background-color: currentColor;
`;

export const LoadingTitle = styled(Typography.Title)``;
