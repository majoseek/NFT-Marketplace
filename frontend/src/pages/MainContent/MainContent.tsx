import * as Styled from './MainContent.styles';

type Props = {
    children: React.ReactNode;
};

const MainContent = ({ children }: Props) => {
    return <Styled.Container>{children}</Styled.Container>;
};

export default MainContent;
