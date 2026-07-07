import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('merchant')
export class Merchant {
    @PrimaryGeneratedColumn('uuid')
    id!: string;

    @Column()
    email!: string;

    @Column()
    password!: string;

    // is email verified?
    @Column()
    verified!: boolean;

    @Column()
    businessName!: string;

    @Column()
    billingAddress!: string;

    @Column()
    billingCity!: string;

    @Column()
    billingState!: string;

    @Column()
    billingZip!: string;
}
