import { useState } from "react";
import type { LoginRequest } from "../../../dto/user/LoginRequest";
import type { SignupRequest } from "../../../dto/user/SignupRequest";
import { emailValidator } from "../../../utils/validators/emailValidator";
import { passwordValidator } from "../../../utils/validators/passwordValidator";
import { useLoginForm } from "../../../hooks/auth/useLoginForm";
import { useSignupForm } from "../../../hooks/auth/useSignupForm";

type UserAuthFormProps = 
    | {
          mode: "login";
          onSubmit: (request: LoginRequest) => Promise<void>;
          userRole: string;
      }
    | {
          mode: "signup";
          onSubmit: (request: SignupRequest) => Promise<void>;
          userRole: string;
      };


export function UserAuthForm({ mode, onSubmit, userRole }: UserAuthFormProps) {
    // login & signup shared
    const { loginForm, setEmail, setPassword } = useLoginForm();

    // signup only
    const { signupForm, setAddress, setCity, setState, setZip } = useSignupForm();

    // to display error
    const [error, setError] = useState("");

    function handleSubmit(event: React.SubmitEvent): void {
        event.preventDefault(); // cancels refreshing back to initial states

        if (!emailValidator(loginForm.email)) {
            setError("Email not set.");
            return;
        }

        if (!passwordValidator(loginForm.password)) {
            setError("Password cannot include symbols.");
            return;
        }
        
        if (mode === "login") {
            const request: LoginRequest = {
                email: loginForm.email,
                password: loginForm.password,
                role: userRole,
            };
            onSubmit(request);
        } else {
            const request: SignupRequest = {
                email: loginForm.email,
                password: loginForm.password,
                // will always be false on signup
                // without verifying email, they won't be able to do anything except log in
                verified: false,
                billingAddress: signupForm.address,
                billingCity: signupForm.city,
                billingState: signupForm.state,
                billingZip: signupForm.zip,
            };
            onSubmit(request);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <input 
            title="Email" type="email" value={loginForm.email} 
            placeholder="Enter Gmail" onChange={(event) => setEmail(event.target.value)}
            />
            <input 
            title="Password" type="password" value={loginForm.password}
            placeholder="Enter password. Mininmum 6, maximum 16 characters. No symbols."
            minLength={6} maxLength={16}
            onChange={(event) => setPassword(event.target.value)}
            />
            {mode === "signup" && (
                <>
                    <br />
                    <input 
                        title="Address" type="text" value={signupForm.address}
                        placeholder="Type your address. No city, state, and zip. Must be in the US."
                        onChange={(event) => setAddress(event.target.value)}
                    />
                    <input 
                        title="City" type="text" value={signupForm.city}
                        placeholder="City"
                        onChange={(event) => setCity(event.target.value)}
                    />
                    <input 
                        title="State" type="text" value={signupForm.state}
                        placeholder="State"
                        onChange={(event) => setState(event.target.value)}
                    />
                    <input 
                        title="Zip" type="text" value={signupForm.zip}
                        placeholder="Zip code"
                        onChange={(event) => setZip(event.target.value)}
                    />
                </>
            )}
            <div hidden={error === "" ? true : false}>
                <br />
                <p>{error}</p>
            </div>
            <br />
            <button>{mode.toUpperCase()}</button>
        </form>
    );
}