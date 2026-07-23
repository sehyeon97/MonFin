import { useState } from "react";
import { UserAuthForm } from "../../components/forms/auth/UserAuthForm";
import type { LoginRequest } from "../../dto/user/LoginRequest";
import { LoginUser } from "../../api/auth/LoginUser";
import { UserTypes } from "../../types/UserType";
import { SignupUser } from "../../api/auth/SignupUser";
import type { SignupRequest } from "../../dto/user/SignupRequest";
import { useNavigate } from "react-router-dom";

export function LoginPortal() {
    const [mode, setMode] = useState("login");
    const [checked, setChecked] = useState(false);
    const [userType, setUserType] = useState(UserTypes.Customer);
    const [error, setError] = useState("");

    const navigate = useNavigate();

    function handleModeChange(mode: "login" | "signup"): void {
        setMode(mode);
    }

    function toggleCheckbox() {
        setChecked(!checked);
        setUserType(checked ? UserTypes.Merchant : UserTypes.Customer);
    }

    async function loginUser(request: LoginRequest) {
        // user ID could be customer ID or merchant ID
        const userID: string = await LoginUser(request);
        if (userID === "Invalid email or password.") {
            setError("Could not find user with the credentials provided.")
        } else {
            routeUser();
        }
    }

    async function signupUser(request: SignupRequest) {
        const data = await SignupUser(request, userType);
        
        if (data.id) {
            // at the moment, data is an object (customer | merchant),
            // so we want the user to log in again after creating an account,
            // so we can retrieve the customer id
            setError("Signup successful. Please log in again to continue.");
        } else {
            setError("Signup failed. Please try again.") // be more specific why, later
        }
    }

    function routeUser() {
        if (userType === UserTypes.Customer) {
            navigate("/shopping")
        } else {
            navigate("/my-business")
        }
    }

    return (
        <div>
            {mode === "login" && (
                <>
                    <title>Login</title>
                    <UserAuthForm mode={mode} onSubmit={loginUser} userRole={userType} />
                </>
            )}

            {mode === "signup" && (
                <>
                    <title>Signup</title>
                    <UserAuthForm mode={mode} onSubmit={signupUser} userRole={userType} />
                </>
            )}
            
            <div>
                <input type="checkbox" checked={checked} onChange={toggleCheckbox}></input>
                <label>I am Merchant</label>
                <br/>
                <button onClick={() => handleModeChange("login")}>Login</button>
                <button onClick={() => handleModeChange("signup")}>Signup</button>
                <br/>
                <p>{error}</p>
            </div>
        </div>
    );
}