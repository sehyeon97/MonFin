import { IsString } from 'class-validator';

export class AllProductsRequest {
    @IsString()
    businessName!: string;
}
