import styled from 'styled-components';
import { Link } from 'react-router-dom';

export const Wrapper = styled.section`
    width: 100%;
    height: 90px;
    background-color: ${({ theme }) => theme.backgroundColors.tertiary};
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 16px;
    border-bottom: 2px solid ${({ theme }) => theme.backgroundColors.light};
`;

export const Logo = styled.img`
    width: 150px;
    margin-right: 24px;
`;

export const MenuLinks = styled.div`
    display: flex;
    gap: 32px;
    align-items: center;
`;

export const MenuNavLink = styled(Link)`
    text-decoration: none;
    color: ${({ theme }) => theme.textColors.primary};
    font-family: 'Fractul';
    transition: all 0.2s;
    white-space: nowrap;

    &:hover {
        color: ${({ theme }) => theme.textColors.secondary};
    }
`;
