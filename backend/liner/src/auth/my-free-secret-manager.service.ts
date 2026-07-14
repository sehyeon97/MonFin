/**
 * 1. Creates Secret Keys
 * 2. Updates Secret Keys
 * 3. Replaces Secret Keys when necessary
 * * Used to create JWTs
 * * JWTs are tied to user-frontend sessions
 * * Every 10min, ask user if they are still there
 * * No response, log them out in 1min
 * * If still there, replace token, continue user-session
 */
export class SecretManager {
    constructor() {}

    public createSecret() {}
}
