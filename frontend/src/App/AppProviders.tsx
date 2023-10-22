import {
    MutationCache,
    QueryCache,
    QueryClient,
    QueryClientProvider,
} from '@tanstack/react-query';
import axios from 'axios';
import { BrowserRouter } from 'react-router-dom';
import { useAuthStore } from '../stores/AuthStore';
import { Provider as ReduxProvider } from 'react-redux';
import { store } from '@/store/store';

type Props = {
    children: JSX.Element;
};

axios.defaults.withCredentials = true;

const axiosErrorCallback = (error: any, query: any) => {
    if (error.response.status === 401) {
        useAuthStore.getState().logoutUser();
    }
};

const queryClient = new QueryClient({
    queryCache: new QueryCache({
        onError: axiosErrorCallback,
    }),
    mutationCache: new MutationCache({
        onError: axiosErrorCallback,
    }),
});

const AppProviders = ({ children }: Props) => (
    <ReduxProvider store={store}>
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>{children}</BrowserRouter>
        </QueryClientProvider>
    </ReduxProvider>
);
export default AppProviders;
