import {
    Column,
    CreateDateColumn,
    Entity,
    PrimaryGeneratedColumn,
} from 'typeorm';

@Entity('customers')
export class Customer {
    @PrimaryGeneratedColumn('uuid')
    id!: string; // No built-in JS/TS UUID data type

    @Column()
    email!: string;

    @Column()
    password!: string;

    @Column()
    billingAddress!: string;

    @Column()
    billingCity!: string;

    @Column()
    billingState!: string;

    @Column()
    billingZip!: string;

    @CreateDateColumn()
    createdAt!: Date;
}
