package com.example.mywifiapp2;

public class WifiAP {


    private String BSSID;

    private double level;

    public WifiAP(String BSSID, double level) {

        this.BSSID = BSSID;
        this.level = level;

    }

    public WifiAP() {

    }

    public String getBSSID() {
        return BSSID;
    }



    public double getLevel() {
        return level;
    }
}
