import { useMutation } from "@tanstack/react-query";
import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import loginImage from "../../assets/images/loginRegisterImage.png";
import { useAuthStore } from "../../stores/AuthStore";

const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { loginUser } = useAuthStore();
  const navigate = useNavigate();

  async function performLogin({ email, password }: any) {
    const response = await axios.post(`/api/auth/login`, {
      username: email,
      password: password,
    });

    return response.data;
  }

  const loginMutation = useMutation(performLogin, {
    onSuccess: (data) => {
      loginUser(data.accountId, data.name);
      navigate("/browse");
    },
    onError: (error) => {
      alert("Invalid username or password!");
    },
  });

  const onLoginClick = () => {
    loginMutation.mutate({ email: username, password: password });
  };

  return (
    <>
      <main className="flex flex-row justify-center items-center p-20">
        <img src={loginImage} alt="login" className="rounded-xl" />
        <section className="p-14 flex flex-col">
          <span className="font-semibold text-4xl">Login</span>
          <span className="text-xl mt-4">
            Enter your account details
            <br />
            to start exploring available auctions
          </span>
          <input
            type="text"
            placeholder="Email address"
            className="input input-bordered w-full max-w-xs rounded-2xl mt-10"
            onChange={(e) => setUsername(e.target.value)}
            value={username}
          />
          <input
            type="password"
            placeholder="Password"
            className="input input-bordered w-full max-w-xs rounded-2xl mt-5"
            onChange={(e) => setPassword(e.target.value)}
            value={password}
          />
          <button className="btn px-16 btn-primary mt-16 ml-6 w-fit" onClick={onLoginClick}>
            Login
          </button>
        </section>
      </main>
    </>
  );
};

export default LoginPage;
