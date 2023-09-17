import { Button as ButtonAntd } from 'antd';
import styled from 'styled-components';

export const Button = styled(ButtonAntd)`
    &&& {
        height: 40px;
        background: linear-gradient(
            90deg,
            rgba(120, 52, 243, 1) 0%,
            rgba(65, 17, 150, 1) 100%
        );
        color: #e9e1fd;
        border: none;
        font-family: 'Roboto', sans-serif;
        font-family: 'Space Mono', monospace;
        font-weight: 600;

        &:hover {
            transform: scale(1.1);
        }
    }
`;
