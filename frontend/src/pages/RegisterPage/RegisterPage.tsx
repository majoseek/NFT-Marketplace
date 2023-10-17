import { useMutation, useQuery } from "@tanstack/react-query";
import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_KEYS } from "../../api/API_KEYS";
import registerImage from "../../assets/images/loginRegisterImage.png";
import { useWallet } from "../../hooks/useWallet";
import { useAuthStore } from "../../stores/AuthStore";

const RegisterPage = () => {
  const { loginUser } = useAuthStore();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [walletQueryEnabled, setWalletQueryEnabled] = useState(false);

  async function performRegister({ username, email, password }: any) {
    const response = await axios.post(`/api/account`, {
      name: username,
      password: password,
      email: email,
      role: "NORMAL_USER"
    });

    return response.data;
  }

  const { mutateAsync: mutateRegister, data: registerData } = useMutation(performRegister, {
    onSuccess: (data) => {
      setShowModal(true);
      createWalletMutation.mutateAsync({ accountId: data.accountId });
    },
    onError: (error) => {
      alert("Invalid username or password!");
    },
  });

  async function performCreateWallet({ accountId }: any) {
    const response = await axios.post(`/api/account/wallet`);
    return response.data;
  }

  const createWalletMutation = useMutation(performCreateWallet, {
    onSuccess: (data) => {
      setWalletQueryEnabled(true);
    },
    onError: (error) => {
      alert("Server error. Please try again later...");
      setShowModal(false);
    },
  });

  // this is duplicated from the useWallet hook, but we needed onSuccess / enabled properties
  const walletQuery = useQuery(['login-wallet-query'], 
    () => axios.get("/api/account/wallet").then((response) => response),
    {
      onSuccess: (data) => {
        if (data.data.walleAddress) {
          setShowModal(false);
          loginUser(registerData.id, registerData.username);
          navigate("/browse");
        }
      },
      cacheTime: 0,
      enabled: walletQueryEnabled,
      refetchInterval: 500,
    }
  );

  const canSignUp =
    username.length > 0 &&
    email.length > 0 &&
    password.length >= 8 &&
    confirmPassword === password;

  const handleRegisterClick = () => {
    mutateRegister({ username, email, password });
  }

  return (
    <main className="flex flex-row justify-center items-center p-20">
      <img src={registerImage} alt="register" className="rounded-xl" />
      <section className="p-14 flex flex-col">
        <span className="font-semibold text-4xl">Create account</span>
        <span className="text-xl mt-4">
          Enter your details and start creating,
          <br />
          collecting and selling NFTs.
        </span>
        <input
          type="text"
          placeholder="Username"
          className="input input-bordered w-full max-w-xs rounded-2xl text-white mt-10"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="text"
          placeholder="Email address"
          className="input input-bordered w-full max-w-xs rounded-2xl text-white mt-5"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          className="input input-bordered w-full max-w-xs rounded-2xl text-white mt-5"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          type="password"
          placeholder="Confirm password"
          className="input input-bordered w-full max-w-xs rounded-2xl text-white mt-5"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />
        <button
          className="btn px-16 btn-primary mt-16 ml-10 w-fit"
          disabled={!canSignUp}
          onClick={() => handleRegisterClick()}
        >
          Create account
        </button>
        <a href="/login" className="ml-12 mt-2 underline">
          Already have an account? Sign in
        </a>
      </section>

      <input type="checkbox" id="progress-modal" className="modal-toggle" checked={showModal} />
      <div className="modal">
        <div className="modal-box">
          <h1 className="font-bold text-2xl text-center">Creating account...</h1>
          <p className="py-4 mt-2 text-center">Hang on for a second while we finish creating your profile.</p>
          <progress className="progress progress-secondary w-full"></progress>
        </div>
      </div>
    </main>
  );
};

export default RegisterPage;
