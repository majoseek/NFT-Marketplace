import styled from 'styled-components';
import bgImage from '@/assets/Background.svg';

export const Container = styled.main`
    width: 100%;
    height: 100vh;
    background-image: url(${bgImage});
    background-repeat: no-repeat;
    background-color: currentColor;
`;
