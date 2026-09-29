import { IsNotEmpty } from 'class-validator';

export class AddPaymentMethodRequest {
    @IsNotEmpty()
    cardToken!: string;

    @IsNotEmpty()
    lastFour!: string;

    @IsNotEmpty()
    fullName!: string;

    @IsNotEmpty()
    network!: string;

    @IsNotEmpty()
    expMonth!: number;

    @IsNotEmpty()
    expYear!: number;
}
