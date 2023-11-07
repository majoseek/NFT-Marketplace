import { useAppSelector } from '@/hooks/useAppSelector';
import ErrorPage from '@/pages/ErrorPage';
import LandingPage from '@/pages/LandingPage';
import LoadingPage from '@/pages/LoadingPage';

type Props = {
    children: React.ReactNode;
};

const ProtectedRoute = ({ children }: Props) => {
    const hasMetaMaskProvider = useAppSelector(
        (state) => state.app.hasMetaMaskProvider
    );
    const wallets = useAppSelector((state) => state.app.wallets);

    return (
        <>
            {hasMetaMaskProvider === undefined ? (
                <LoadingPage title="Loading MetaMask..." />
            ) : hasMetaMaskProvider === false ? (
                <ErrorPage title="No MetaMask detected" />
            ) : wallets && wallets.length === 0 ? (
                <LandingPage />
            ) : (
                children
            )}
        </>
    );
};
export default ProtectedRoute;
