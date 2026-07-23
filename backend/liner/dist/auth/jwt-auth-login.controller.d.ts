import { AuthService } from './jwt-auth-login.service';
import { LoginUserRequest } from './login-user.request-dto';
import type { Response } from 'express';
export declare class AuthController {
    private readonly authService;
    constructor(authService: AuthService);
    loginUser(req: LoginUserRequest, res: Response): Promise<string>;
}
