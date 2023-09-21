import * as Styled from './ErrorPage.styles';

type Props = {
    title?: React.ReactNode;
};

const ErrorPage = ({ title }: Props) => (
    <Styled.Wrapper>
        <Styled.ErrorIcon />
        {title && <Styled.ErrorTitle level={3}>{title}</Styled.ErrorTitle>}
    </Styled.Wrapper>
);

export default ErrorPage;
