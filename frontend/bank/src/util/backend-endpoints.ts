export const domain = `${process.env.NEXT_PUBLIC_BANK_DOMAIN_NAME}`;

export const authGateway = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_CONTROLLER}`;
export const bankGateway = `${process.env.NEXT_PUBLIC_BANK_CONTROLLER}`;
export const transactionGateway = `${process.env.NEXT_PUBLIC_TRANSACTION_CONTROLLER}`;

export const signupEndpoint = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_REGISTER_ACCOUNT}`;
export const loginEndpoint = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_LOGIN_USER}`;
export const validateReturningEndpoint = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_IS_JWT_VALID_STILL}`;
export const refreshEndpoint = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_REFRESH_ACCESS_TOKEN}`;
export const logoutEndpoint = `${process.env.NEXT_PUBLIC_BANK_AUTHENTICATION_LOGOUT}`;

export const createCardEndpoint = `${process.env.NEXT_PUBLIC_BANK_CONTROLLER_CREATE_CARD}`;
export const activateCardEndpoint = `${process.env.NEXT_PUBLIC_BANK_ACTIVATE_CARD}`;
export const ownedCardsEndpoint = `${process.env.NEXT_PUBLIC_BANK_OWNED_CARDS}`;
export const deleteCardEndpoint = `${process.env.NEXT_PUBLIC_BANK_DELETE_CARD}`;

export const authorizeTransactionEndpoint = `${process.env.NEXT_PUBLIC_TRANSACTION_CONTROLLER_AUTHORIZE_TRANSACTION}`;
export const verifyOTPEndpoint = `${process.env.NEXT_PUBLIC_TRANSACTION_CONTROLLER_VERIFY_OTP}`;
export const transactionHistoryEndpoint = `${process.env.NEXT_PUBLIC_TRANSACTION_HISTORY}`;
