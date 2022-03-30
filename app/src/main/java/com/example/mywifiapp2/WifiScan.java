package com.example.mywifiapp2;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

public class WifiScan {

    // make use of WifiManager to get the available Wifi APs nearby

    /**
     * for wifi scan result:
     * BSSID: address of the access point (MAC address)
     * SSID: network name
     * level: detected signal level in dBm (RSSI)*/

    private static Context applicationContext, activityContext;
    private static List<ScanResult> scanList;

    public WifiScan(Context applicationContext, Context activityContext){
        this.applicationContext = applicationContext;
        this.activityContext = activityContext;
    }


    public void getWifiNetworksList() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
        applicationContext.registerReceiver(new BroadcastReceiver() {

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onReceive(Context context, Intent intent) {
                scanList = wifiManager.getScanResults();
            }

        }, filter);

        boolean startScan = wifiManager.startScan();
        if(!startScan){
            Toast.makeText(activityContext,"Please Enable Access of Location",Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activityContext.startActivity(myIntent);
        }

    }

    public List<ScanResult> getScanList(){
        return scanList;
    }


}
