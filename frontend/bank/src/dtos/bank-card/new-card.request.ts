export class NewCardRequest {
  jwt!: string; // should be JWT later. atm is bank account id
  cardType!: string;
  cardNetwork!: string;
  cardTier!: string;
}
