import { ButtonProps } from 'antd';
import * as Styled from './Button.styles';

type Props = { children: React.ReactNode } & ButtonProps;

const Button = ({ children, ...rest }: Props) => (
    <Styled.Button {...rest}>{children}</Styled.Button>
);

export default Button;
