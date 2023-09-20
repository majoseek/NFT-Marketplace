import { Spin } from 'antd';
import * as Styled from './LoadingPage.styles';

type Props = {
    title?: React.ReactNode;
};

const LoadingPage = ({ title }: Props) => (
    <Styled.Wrapper>
        <Spin indicator={<Styled.LoadingSpin />} />
        {title && <Styled.LoadingTitle>{title}</Styled.LoadingTitle>}
    </Styled.Wrapper>
);

export default LoadingPage;
