import {
    CanActivate,
    ExecutionContext,
    Injectable,
    UnauthorizedException,
} from '@nestjs/common';
import { AuthService } from './jwt-auth-login.service';
import { AuthenticatedRequest } from './jwt-auth-guard.dto';

@Injectable()
export class JwtAuthGuard implements CanActivate {
    constructor(private readonly authService: AuthService) {}

    async canActivate(context: ExecutionContext): Promise<boolean> {
        const request = context
            .switchToHttp()
            .getRequest<AuthenticatedRequest>();

        console.log(request.cookies);

        const token = request.cookies?.access_token;

        if (!token) {
            throw new UnauthorizedException('token invalid');
        }

        const payload = await this.authService.verifyAccessJWT(token);

        if (!payload) {
            throw new UnauthorizedException('payload invalid');
        }

        request.user = payload;

        return true;
    }
}
