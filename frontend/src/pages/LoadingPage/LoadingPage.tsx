import LoadingElement from '@/components/LoadingElement';

type Props = {
    title?: string | React.ReactNode;
};

const LoadingPage = ({ title }: Props) => (
    <main className="p-20 flex gap-7 justify-center items-center">
        <LoadingElement title={title} />
    </main>
);

export default LoadingPage;
