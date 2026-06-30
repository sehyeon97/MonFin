package com.sehyeon.monfin.bank.model.entity.bank;

/**
 * Will be used later to handle geographically impossible transactions
 * on mobile purchases using Wallet
 */
public class Location {

    private String cityName;

    protected Location() {}

    public Location(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }
    
}
