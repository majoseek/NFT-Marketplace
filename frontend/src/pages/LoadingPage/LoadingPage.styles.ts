import { LoadingOutlined } from '@ant-design/icons';
import { Typography } from 'antd';
import styled from 'styled-components';

export const LoadingSpin = styled(LoadingOutlined)`
    &&& {
        font-size: 64px;
        color: ${({ theme }) => theme.textColors.teritary};
    }
`;

export const Wrapper = styled.div`
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    gap: 32px;
    background-color: ${({ theme }) => theme.backgroundColors['primary']};
`;

export const LoadingTitle = styled(Typography.Title)`
    && {
        color: ${({ theme }) => theme.textColors.primary};
    }
`;
