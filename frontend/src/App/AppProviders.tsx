import { QueryClient, QueryClientProvider } from 'react-query';
import { BrowserRouter } from 'react-router-dom';
import { Provider as ReduxProvider } from 'react-redux';
import { store } from '@/store/store';

type Props = {
    children: JSX.Element;
};

const queryClient = new QueryClient();

const AppProviders = ({ children }: Props) => (
    <ReduxProvider store={store}>
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>{children}</BrowserRouter>
        </QueryClientProvider>
    </ReduxProvider>
);
export default AppProviders;
