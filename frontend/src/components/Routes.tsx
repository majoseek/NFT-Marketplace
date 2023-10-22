import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../stores/AuthStore';
import { useMarketplaceStore } from '../stores/MarketplaceStore';

export const AuthProtectedRoutes = () => {
    const { isUserLoggedIn } = useAuthStore();

    return isUserLoggedIn() ? <Outlet /> : <Navigate to="/login" />;
};

export const SchoolProtectedRoutes = () => {
    const { isSchoolChosen } = useMarketplaceStore();

    return isSchoolChosen() ? <Outlet /> : <Navigate to="/school" />;
};

export const BrowseRoute = () => {
    const { schoolId } = useMarketplaceStore();

    return <Navigate to={`/browse/${schoolId}`} />;
};
