import styled from 'styled-components';

export const Container = styled.main`
    background-color: ${({ theme }) => theme.backgroundColors.primary};
    padding: 32px;
    height: calc(100vh - 173px);
`;
