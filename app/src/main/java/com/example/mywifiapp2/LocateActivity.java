package com.example.mywifiapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LocateActivity extends AppCompatActivity {
    private ArrayList<WifiAP> listofWifiAP = new ArrayList<>();
    private ArrayList<String> arrayListLocation = new ArrayList<>();
    private ArrayList<String> arrayListWifiAPs = new ArrayList<>();
    private ArrayList<String> arrayListBSSID = new ArrayList<>();
    private ArrayList<Object> arrayListofstuff = new ArrayList<>();
    private WifiManager wifiManager;
    private Testing locator;
    private TextView currentPosition;
    private Point currentCoordinates;
    private Button locateMe;
    private ListView listView;
    private Button buttonScan;
    private EditText locationName;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<WifiAP> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
//    private HashMap locationFirebase;

//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
//        buttonScan = findViewById(R.id.scan);
//        locationName = findViewById(R.id.locationName);
//        buttonScan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                scanWifi();
//            }
//        });
//        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        currentPosition = findViewById(R.id.currentLocation);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Wifi is disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
//        listView.setAdapter(adapter);
//
//
        scanWifi();

        locateMe = findViewById(R.id.locateme);

        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                locator = new Testing(results);
//                currentCoordinates = locator.getPrediction();
//                currentPosition.setText("You are currently positioned at coordinates" + currentCoordinates);



            }
        });











    }

    private void scanWifi() {

            // scan without any restrictions

            arrayList.clear();
            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            wifiManager.startScan();
            Toast.makeText(this, "Scanning Wifi...", Toast.LENGTH_SHORT).show();



    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
//                arrayList.add(" BSSID: \n" + scanResult.BSSID + "\n" + " SSID: \n" + scanResult.SSID + "\n" + " Description: \n" + scanResult.capabilities + "\n" + " RSSI: \n" + scanResult.level);
//                adapter.notifyDataSetChanged();
                arrayList.add(new WifiAP(scanResult.BSSID, scanResult.level));
            }
        }
    };

}



