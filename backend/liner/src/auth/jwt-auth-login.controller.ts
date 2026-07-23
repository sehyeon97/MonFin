import { Body, Controller, Post, Res } from '@nestjs/common';
import { AuthService } from './jwt-auth-login.service';
import { LoginUserRequest } from './login-user.request-dto';
import type { Response } from 'express';

@Controller('payment-api/auth')
export class AuthController {
    constructor(private readonly authService: AuthService) {}

    // Takes username and password as a request object
    // returns jwt string if valid credentials
    @Post('login')
    public async loginUser(
        @Body() req: LoginUserRequest,
        @Res({ passthrough: true }) res: Response,
    ): Promise<string> {
        // if login failed, it would stop here
        const token: string = await this.authService.signJWT(req);

        res.cookie('access_token', token, {
            httpOnly: true,
            secure: false, // when testing under http localhost, set false
            sameSite: 'lax',
            maxAge: 15 * 60 * 1000,
        });

        return 'Login successful';
    }
}
