import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CardTokenizationService } from '../services/card-tok.service';
import { CardTokenizationRequest } from '../models/card-tok-request';
import { CardTokenizationResponse } from '../models/card-tok-response';

@Component({
  selector: 'card-tokenization-form',
  templateUrl: './card-tok-form.component.html',
  styleUrls: ['./card-tok-form.component.css'],
  imports: [FormsModule],
  standalone: true,
})
export class CardTokenizationFormComponent {
  readonly paymentFrontend = 'http://localhost:5173';

  constructor(private readonly cardTokenizationService: CardTokenizationService) {}

  pan = '';
  cvv = '';
  fullName = '';
  expMonth = '';
  expYear = '';

  addCardToAccount() {
    // create request to send to tsp backend
    const request: CardTokenizationRequest = {
      pan: this.pan,
      cvv: this.cvv,
      fullName: this.fullName,
      expMonth: this.expMonth,
      expYear: this.expYear,
    };

    // use service to request tokenization
    // receive and forward response to payment processor frontend
    this.cardTokenizationService.tokenizeCard(request).subscribe((response) => {
      const dataToSend: CardTokenizationResponse = {
        tokenized: response.tokenized,
        message: response.message,
        cardToken: response.cardToken,

        lastFour: this.pan.substring(12),
        fullName: this.fullName,
        network: response.network,
        expMonth: Number(this.expMonth),
        expYear: Number(this.expYear),
      };
      window.parent.postMessage(response, this.paymentFrontend);
    });
  }
}
