import {
    Column,
    CreateDateColumn,
    Entity,
    PrimaryGeneratedColumn,
} from 'typeorm';

@Entity('purchased-item')
export class PurchasedItem {
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
}
