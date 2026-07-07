import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity('card-vault')
export class CardVault {
    @PrimaryGeneratedColumn('uuid')
    id!: string;

    // can be customerID or merchantID
    @Column()
    userID!: string;

    @Column()
    cardToken!: string;

    @Column()
    lastFour!: string;

    @Column()
    fullName!: string;

    @Column()
    network!: string;

    @Column()
    expMonth!: number;

    @Column()
    expYear!: number;

    @Column()
    lastUsedAt!: string;
}
