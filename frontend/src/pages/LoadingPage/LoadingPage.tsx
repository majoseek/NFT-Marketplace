import LoadingElement from '@/components/LoadingElement';

type Props = {
    title?: string | React.ReactNode;
    description?: string | React.ReactNode;
};

const LoadingPage = ({ title, description }: Props) => (
    <main className="p-20 flex gap-7 justify-center items-center flex-col">
        <LoadingElement title={title} />
        <span className="italic">{description}</span>
    </main>
);

export default LoadingPage;
