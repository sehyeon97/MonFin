import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CardTokenizationRequest } from '../models/card-tok-request';
import { CardTokenizationResponse } from '../models/card-tok-response';
import { Observable } from 'rxjs';

/**
 * Makes an api call to bank/tsp backend and returns the result to the tied component
 */
@Injectable({
  providedIn: 'root',
})
export class CardTokenizationService {
  private readonly tspUrl: string = '/api/bank/payment/processor/credentials';

  constructor(private readonly http: HttpClient) {}

  tokenizeCard(request: CardTokenizationRequest): Observable<CardTokenizationResponse> {
    return this.http.post<CardTokenizationResponse>(this.tspUrl, request);
  }
}
