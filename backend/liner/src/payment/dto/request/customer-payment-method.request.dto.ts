import { IsNotEmpty, IsUUID } from 'class-validator';

export class AddPaymentMethodRequest {
    @IsUUID()
    @IsNotEmpty()
    customerID!: string;

    @IsNotEmpty()
    cardToken!: string;

    lastFour!: string;

    fullName!: string;

    network!: string;

    expMonth!: number;

    expYear!: number;
}
