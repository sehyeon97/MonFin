import { IsNotEmpty } from 'class-validator';

export class AddPaymentMethodRequest {
    @IsNotEmpty()
    cardToken!: string;

    lastFour!: string;

    fullName!: string;

    network!: string;

    expMonth!: number;

    expYear!: number;
}
