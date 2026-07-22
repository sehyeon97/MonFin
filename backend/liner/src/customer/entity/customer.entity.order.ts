import {
    Column,
    CreateDateColumn,
    Entity,
    PrimaryGeneratedColumn,
} from 'typeorm';

@Entity('customer-order')
export class Order {
    @PrimaryGeneratedColumn('uuid')
    id!: string;

    // bought by
    @Column()
    customerID!: string;

    // bought from
    @Column()
    merchantID!: string;

    // bought with
    @Column()
    cardToken!: string;

    @CreateDateColumn()
    boughtAt!: string;

    @Column()
    transactionID!: string;

    @Column()
    merchantName!: string;

    @Column()
    productName!: string;

    @Column()
    itemCount!: number;

    @Column()
    totalPrice!: number;

    @Column()
    brand!: string;

    @Column()
    count!: number;

    @Column()
    isApproved!: boolean;
}
