import { useNavigate } from "react-router-dom";
import { ReactComponent as RocketIcon } from "../../assets/icons/rocketIcon.svg";
import { ReactComponent as LandingImage } from "../../assets/images/landingImage.svg";
import userAvatar from "../../assets/images/userAvatar.png";
import { useAuthStore } from "../../stores/AuthStore";
import { useMarketplaceStore } from "../../stores/MarketplaceStore";

const LandingPage = () => {
  const navigate = useNavigate();
  const { isUserLoggedIn } = useAuthStore();
  const { isSchoolChosen } = useMarketplaceStore();

  const handleGetStarted = () => {
    if (isUserLoggedIn()) {
      if (isSchoolChosen()) {
        // this will redirect to /browse/schoolId
        navigate("/browse");
      } else {
        navigate("/school");
      }
    } else {
      navigate("/register");
    }
  };

  return (
    <>
      <main className="p-20 flex gap-7 justify-center items-center">
        <section className="flex flex-col">
          <span className="font-semibold text-5xl mb-4 leading-tight">
            Discover
            <br />
            Digital Work &
            <br />
            Collect Items
          </span>
          <span className="text-base max-w-md">
            NFT Marketplace is designed for students' school works. Collect, buy
            and sell works from students of your chosen school!
          </span>
          <button
            onClick={handleGetStarted}
            className="btn btn-primary mt-24 w-fit"
          >
            <RocketIcon className="mr-3" />
            Get started
          </button>
          <div className="flex justify-between mt-8 max-w-sm">
            <div className="flex flex-col ">
              <span className="font-semibold text-2xl text-center">15+</span>
              <span className="text-lg">Total auctions</span>
            </div>
            <div className="flex flex-col ">
              <span className="font-semibold text-2xl text-center">3+</span>
              <span className="text-lg">Schools</span>
            </div>
            <div className="flex flex-col ">
              <span className="font-semibold text-2xl text-center">10+</span>
              <span className="text-lg">Artists</span>
            </div>
          </div>
        </section>
        <section className="hidden md:block">
          <LandingImage />
          <div className="bg-primary p-5 rounded-b-xl">
            <span className="font-medium text-2xl">Space Walking</span>
            <span className="flex mt-3 gap-3 leading-xs items-center">
              <img src={userAvatar} alt="user" />
              Bartek Biga
            </span>
          </div>
        </section>
      </main>
    </>
  );
};
export default LandingPage;
