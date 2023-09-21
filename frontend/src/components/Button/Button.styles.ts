import { Button as ButtonAntd } from 'antd';
import styled from 'styled-components';

export const Button = styled(ButtonAntd)`
    &&& {
        width: fit-content;
        height: 40px;
        background: linear-gradient(
            90deg,
            ${({ theme }) => theme.colors.primary} 0%,
            ${({ theme }) => theme.colors.primaryDark} 100%
        );
        color: ${({ theme }) => theme.textColors.primary};
        border: none;
        font-family: 'Roboto', sans-serif;
        font-family: 'Space Mono', monospace;
        font-weight: 600;

        &:hover {
            transform: scale(1.05);
        }
    }
`;
