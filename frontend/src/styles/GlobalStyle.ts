import { createGlobalStyle } from 'styled-components';

export const GlobalStyle = createGlobalStyle`
body {
    margin:0;
}

.ant-dropdown-menu{
    background-color: ${({ theme }) => theme.backgroundColors.light} !important;

    .ant-dropdown-menu-item{
        padding: 12px !important;
    }
}
`;
