type Props = {
    title?: string | React.ReactNode;
    className?: string;
};

const LoadingElement = ({ title, className }: Props) => {
    return (
        <div
            className={`flex flex-col justify-center items-center gap-6 ${className}`}
        >
            {title && <span className="font-semibold text-2xl">{title}</span>}
            <span className="loading loading-bars loading-lg"></span>
        </div>
    );
};
export default LoadingElement;
