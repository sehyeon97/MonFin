import { IsNotEmpty, IsUUID } from 'class-validator';

export class AddMerchantDebitCardRequest {
    @IsUUID()
    @IsNotEmpty()
    merchantID!: string;

    @IsNotEmpty()
    cardToken!: string;

    lastFour!: string;

    fullName!: string;

    network!: string;

    expMonth!: number;

    expYear!: number;
}
