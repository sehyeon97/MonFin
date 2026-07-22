import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('company')
export class MerchantCompany {
    @PrimaryGeneratedColumn('uuid')
    id!: string;

    @Column()
    merchantID!: string;

    @Column()
    businessName!: string;
}
