import * as Styled from './Header.styles';
import LogoImg from '@/assets/nftLogo.png';

const Header = () => (
    <Styled.Wrapper>
        <Styled.Logo src={LogoImg} alt="logo" />
    </Styled.Wrapper>
);

export default Header;
