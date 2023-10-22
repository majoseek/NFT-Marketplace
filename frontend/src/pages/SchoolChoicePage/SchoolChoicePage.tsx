import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import SearchIcon from "../../assets/icons/searchIcon.svg";
import { API_KEYS } from "../../api/API_KEYS";
import axios from "axios";
import { useQuery } from "@tanstack/react-query";
import { useMarketplaceStore } from "../../stores/MarketplaceStore";

const BrowsePage = () => {
  const navigate = useNavigate();
  const { setChosenSchool } = useMarketplaceStore();
  const [schools, setSchools] = useState([]);
  const [searchInput, setSearchInput] = useState("");
  const { data: schoolsResponse, isLoading } = useQuery(
    [API_KEYS.GET_SCHOOLS],
    () => axios.get("/api/school").then((response) => response),
    { onSuccess: (response) => setSchools(response.data) }
  );

  const handleSchoolClick = (schoolId: number) => {
    setChosenSchool(schoolId);
    navigate(`/browse/${schoolId}`);
  };

  const handleChangeInput = (e: any) => {
    e.preventDefault();
    setSearchInput(e.target.value);
  };

  useEffect(() => {
    if (!schoolsResponse) return;
    searchInput !== ""
      ? setSchools(
          schoolsResponse.data.filter((school: any) =>
            school.name.toLowerCase().includes(searchInput.toLowerCase())
          )
        )
      : setSchools(schoolsResponse.data);
  }, [searchInput, schoolsResponse]);

  return (
    <main className="py-32 px-20 flex items-start flex-col justify-center">
      <div className="flex justify-around w-full">
        <span>
          <h3 className="text-3xl font-bold">Discover available schools</h3>
          <h4 className="text-xl mt-3">Explore new trending universities</h4>
        </span>
        <span className="relative">
          <input
            type="text"
            placeholder="Search schools..."
            className="input input-bordered w-full max-w-xs bg-transparent border-primary pr-12"
            onChange={handleChangeInput}
          />
          <img src={SearchIcon} className="absolute top-3 right-3" />
        </span>
      </div>
      <section className="flex gap-10 mt-24 flex-wrap justify-center w-full">
        {isLoading ? (
          <progress className="progress w-56" />
        ) : (
          schools.map(({ schoolId, name, address, photoUrl }) => (
            <div
              className="max-w-xs cursor-pointer"
              key={schoolId}
              onClick={() => handleSchoolClick(schoolId)}
            >
              <img
                src={photoUrl}
                alt="school"
                className="rounded-t-xl object-cover min-h-[240px]"
              />
              <div className="bg-primary p-5 rounded-b-xl text-center hover:bg-primaryHoverFocus/30 flex flex-col gap-2 min-h-xl">
                <span className="font-medium text-xl">{name}</span>
                <span className="">{address}</span>
              </div>
            </div>
          ))
        )}
      </section>
    </main>
  );
};

export default BrowsePage;
