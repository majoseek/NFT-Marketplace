import { Route, Routes } from "react-router-dom";
import Footer from "../components/Footer";
import Header from "../components/Header";
import {
  AuthProtectedRoutes,
  BrowseRoute,
  SchoolProtectedRoutes,
} from "../components/Routes";
import SchoolChoicePage from "../pages/SchoolChoicePage/SchoolChoicePage";
import CreateNftPage from "../pages/CreateNftPage";
import LandingPage from "../pages/LandingPage";
import LoginPage from "../pages/LoginPage";
import NftsPage from "../pages/NftsPage";
import OwnedNftsPage from "../pages/OwnedNftsPage";
import RegisterPage from "../pages/RegisterPage";
import AuctionPage from "../pages/AuctionPage";
import CreateAuctionPage from "../pages/CreateAuctionPage";

const App = () => {
  return (
    <div className="pb-44 relative min-h-screen">
      <Header />
      <Routes>
        <Route element={<LandingPage />} path="/" />
        <Route element={<LoginPage />} path="/login" />
        <Route element={<RegisterPage />} path="/register" />

        <Route element={<AuthProtectedRoutes />}>
          <Route element={<SchoolChoicePage />} path="/school" />

          <Route element={<SchoolProtectedRoutes />}>
            <Route element={<BrowseRoute />} path="/browse" />
            <Route element={<OwnedNftsPage />} path="/ownedNfts" />
            <Route element={<CreateNftPage />} path="/createNft" />
          </Route>

          {/* these aren't school protected - 
           if user visits a link, schoolId will be automatically set */}
          <Route element={<NftsPage />} path="/browse/:schoolId" />
          <Route
            element={<AuctionPage />}
            path="/browse/:schoolId/:auctionId"
          />
          <Route element={<CreateAuctionPage />} path="/sellNft/:nftId" />
        </Route>
      </Routes>
      <Footer />
    </div>
  );
};

export default App;
