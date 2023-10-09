import App from './app/App';
import { ThemeProvider } from 'styled-components';
import { theme } from './styles/theme';
import { GlobalStyle } from './styles/GlobalStyle';
import { Provider as ReduxProvider } from 'react-redux';
import { store } from './store/store';
import ReactDOM from 'react-dom/client';
import { worker } from './mocks/browser';
import '@/styles/fonts.css';
import { RouterProvider, createBrowserRouter } from 'react-router-dom';

if (process.env.NODE_ENV === 'development') {
    worker.start({ onUnhandledRequest: 'bypass' });
}

const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
    },
    {
        path: 'marketplace',
        element: <div>Marketplace</div>,
    },
]);

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);
root.render(
    <ReduxProvider store={store}>
        <ThemeProvider theme={theme}>
            <RouterProvider router={router} />
            <GlobalStyle />
        </ThemeProvider>
    </ReduxProvider>
);
