import * as Styled from './TopMenu.styles';
import LogoImg from '@/assets/nftLogo.png';

const TopMenu = () => (
    <Styled.Wrapper>
        <Styled.MenuLinks>
            <Styled.Logo src={LogoImg} alt="logo-nft" />
            <Styled.MenuNavLink to="marketplace">
                Marketplace
            </Styled.MenuNavLink>
            <Styled.MenuNavLink to="collection">
                My collection
            </Styled.MenuNavLink>
        </Styled.MenuLinks>
    </Styled.Wrapper>
);

export default TopMenu;
