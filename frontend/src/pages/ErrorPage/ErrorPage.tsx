import ErrorElement from '@/components/ErrorElement';

type Props = {
    title?: string | React.ReactNode;
};

const ErrorPage = ({ title }: Props) => (
    <main className="p-20 flex gap-7 justify-center items-center">
        <ErrorElement title={title} />
    </main>
);

export default ErrorPage;
