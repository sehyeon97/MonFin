import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('product')
export class Product {
    @PrimaryGeneratedColumn('uuid')
    id!: string;

    @Column()
    merchantID!: string;

    @Column()
    businessName!: string;

    @Column()
    brand!: string;

    @Column()
    price!: number;

    @Column()
    desc!: string;

    @Column()
    count!: number;
}
