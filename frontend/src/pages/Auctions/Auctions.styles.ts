import Button from '@/components/Button';
import { Space } from 'antd';
import styled from 'styled-components';

export const AuctionsContainer = styled.section`
    display: flex;
    flex-direction: column;
    justify-content: center;
    color: ${({ theme }) => theme.textColors.primary};
    font-family: 'Fractul';
`;

export const DiscoverWrapper = styled.div`
    display: flex;
    justify-content: space-between;
`;

export const TitlesWrapper = styled.div`
    display: flex;
    flex-direction: column;
    gap: 8px;
`;

export const ActionsWrapper = styled.div`
    display: flex;
    gap: 16px;
    align-items: center;
`;

export const DropdownBtn = styled(Space)`
    background-color: ${({ theme }) => theme.backgroundColors.light};
    display: flex;
    justify-content: space-between;
    width: 200px;
    padding: 16px;
    border-radius: 8px;
    font-family: 'Fractul';
    cursor: pointer;
`;

export const FilterBtn = styled(Button)`
    &&&& {
        background: ${({ theme }) => theme.backgroundColors.light};
    }
`;

export const FilterLabel = styled.span`
    color: ${({ theme }) => theme.textColors.primary};
`;

export const DiscoverTitle = styled.span`
    font-size: 1.5rem;
    font-weight: bold;
`;

export const DiscoverDescription = styled.span`
    font-size: 0.8rem;
    color: ${({ theme }) => theme.textColors.tertiary};
`;

export const AuctionWrapper = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
`;
