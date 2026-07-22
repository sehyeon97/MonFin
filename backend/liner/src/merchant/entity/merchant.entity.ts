import {
    Column,
    CreateDateColumn,
    Entity,
    PrimaryGeneratedColumn,
} from 'typeorm';

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
