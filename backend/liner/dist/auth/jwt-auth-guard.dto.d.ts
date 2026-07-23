import { Request } from 'express';
import { JWTAccessPayload } from './jwt-access-payload.dto';
export interface AuthenticatedRequest extends Request {
    cookies: {
        access_token?: string;
    };
    user: JWTAccessPayload;
}
