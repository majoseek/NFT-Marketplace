import { WarnIcon } from '@/assets/icons/WarnIcon';

type Props = {
    title?: string | React.ReactNode;
};

const ErrorElement = ({ title }: Props) => {
    return (
        <div className="flex flex-col justify-center items-center gap-6">
            <WarnIcon />
            {title && <span className="font-semibold text-lg">{title}</span>}
        </div>
    );
};
export default ErrorElement;
