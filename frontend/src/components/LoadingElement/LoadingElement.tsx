type Props = {
    title?: string | React.ReactNode;
};

const LoadingElement = ({ title }: Props) => {
    return (
        <div className="flex flex-col justify-center items-center gap-6">
            {title && <span className="font-semibold text-lg">{title}</span>}
            <span className="loading loading-bars loading-lg"></span>
        </div>
    );
};
export default LoadingElement;
