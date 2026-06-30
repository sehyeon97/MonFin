package com.sehyeon.monfin.bank.model.limits;

import java.util.Map;

import com.sehyeon.monfin.bank.model.card.limits.CardTier;
import com.sehyeon.monfin.bank.model.card.network.CardNetwork;

// Dummy data
public class CardLimits {

    private final Map<CardNetwork, Map<CardTier, SpendingLimits>> cardLimits;

    public CardLimits() {
        this.cardLimits = Map.of(
            CardNetwork.VISA, 
                Map.of(
                    CardTier.BRONZE, new SpendingLimits(150000, 600000), // $1500, $6000
                    CardTier.SILVER, new SpendingLimits(400000, 1500000), // $4000, $15000
                    CardTier.GOLD, new SpendingLimits(1000000, 5000000) // $10000, $50000
                ),
            CardNetwork.MASTERCARD, 
                Map.of(
                    CardTier.BRONZE, new SpendingLimits(75000, 300000), // $750, $3000
                    CardTier.SILVER, new SpendingLimits(200000, 600000), // $2000, $6000
                    CardTier.GOLD, new SpendingLimits(500000, 1500000) // $5000, $15000
                ),
            CardNetwork.DISCOVER, 
                Map.of(
                    CardTier.BRONZE, new SpendingLimits(25000, 100000), // $250, $1000
                    CardTier.SILVER, new SpendingLimits(50000, 200000), // $500, $2000
                    CardTier.GOLD, new SpendingLimits(100000, 250000) // $1000, $2500
                )
        );
    }

    public SpendingLimits getSpendingLimits(CardNetwork network, CardTier tier) {
        return cardLimits.get(network).get(tier);
    }
    
}
