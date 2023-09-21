import { WarningOutlined } from '@ant-design/icons';
import { Typography } from 'antd';
import styled from 'styled-components';

export const ErrorIcon = styled(WarningOutlined)`
    &&& {
        font-size: 64px;
        color: ${({ theme }) => theme.colors.primary};
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

export const ErrorTitle = styled(Typography.Title)`
    && {
        color: ${({ theme }) => theme.textColors.primary};
    }
`;
