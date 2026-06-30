package com.sehyeon.monfin.bank.model.limits;

public class SpendingLimits {
    
    private final int dailyAmount;
    private final int monthlyAmount;

    public SpendingLimits(int dailyAmount, int monthlyAmount) {
        this.dailyAmount = dailyAmount;
        this.monthlyAmount = monthlyAmount;
    }

    public int getDailyAmountLimit() {
        return dailyAmount;
    }

    public int getMonthlyAmountLimit() {
        return monthlyAmount;
    }

}
