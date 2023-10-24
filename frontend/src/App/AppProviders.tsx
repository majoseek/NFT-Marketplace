import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import axios from 'axios';
import { BrowserRouter } from 'react-router-dom';
import { Provider as ReduxProvider } from 'react-redux';
import { store } from '@/store/store';

type Props = {
    children: JSX.Element;
};

axios.defaults.withCredentials = true;

const queryClient = new QueryClient();

const AppProviders = ({ children }: Props) => (
    <ReduxProvider store={store}>
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>{children}</BrowserRouter>
        </QueryClientProvider>
    </ReduxProvider>
);
export default AppProviders;
