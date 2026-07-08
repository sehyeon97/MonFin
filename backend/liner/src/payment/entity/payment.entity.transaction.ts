import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';
import { TRANSACTION_STATUS } from '../enums/transaction-status.enum';

@Entity('transaction')
export class Transaction {
    @PrimaryGeneratedColumn('uuid')
    id!: string;

    @Column()
    cardToken!: string;

    @Column()
    customerID!: string;

    @Column()
    merchantID!: string;

    // java's merchantName = businessName here
    @Column()
    businessName!: string;

    // toISOString() will make it easier for java to convert to instant using Instant.parse()
    @Column()
    timestamp!: string;

    @Column()
    price!: number;

    @Column()
    itemName!: string;

    @Column()
    brand!: string;

    @Column()
    quantity!: number;

    @Column()
    status!: TRANSACTION_STATUS;

    // @Column()
    // cryptogram!: string;

    // // bank calls back this frontend url after sending transaction result to processor's backend
    // @Column()
    // callbackUrl!: string;
}
