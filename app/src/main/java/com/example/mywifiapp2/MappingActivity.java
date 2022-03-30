package com.example.mywifiapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingActivity extends AppCompatActivity {
//
//    private WifiManager wifiManager;
//    private ListView listView;
//    private Button buttonaddPoint;
//    private EditText locationName;
//    private int size = 0;
//    private EditText pointX;
//    private EditText pointY;
//    private List<ScanResult> results;
//    private ArrayList<String> arrayList = new ArrayList<>();
//    private ArrayAdapter adapter;
//    private Mapping mapper = new Mapping();
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference();
    FirebaseUser user;
    DatabaseReference database;
    SubsamplingScaleImageView imageToMap;
    StorageReference storage;
    Uri mImageUri;
    // create reference to firebase, create wifi header



    private StringBuilder stringBuilder = new StringBuilder();
    private TextView textViewWifiNetworks;
    private Button buttonClick;
    private List<ScanResult> scanList;
    private ListView listView_wifiList;
    private ArrayList<String> wifiList;
    private ArrayAdapter arrayAdapter;
    private EditText pointX,pointY, locationName;
    private Point currentCoord;
    private String locationNameString;
    private Button saveMap;
    private Mapping myMapper = new Mapping();
    Point predictedCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapping);
        imageToMap = findViewById(R.id.mappingstuff);
        saveMap = findViewById(R.id.saveMap);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        pointX = findViewById(R.id.pointX);
        pointY = findViewById(R.id.pointY);
        locationName = findViewById(R.id.locationName);
        storage = FirebaseStorage.getInstance().getReference(user.getUid());


        wifiList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,wifiList);
        buttonClick = findViewById(R.id.button_click);



        Bundle b = getIntent().getExtras();
        if ( b!= null){
//            newString = (String) b.get("Imageselected");
//            mImageUri = Uri.parse(newString);
            if (b.getByteArray("IMAGE_DEVICE")!=null){
            byte[] byteArray = b.getByteArray("IMAGE_DEVICE");

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageToMap.setImage(ImageSource.bitmap(bmp));}
            else {
                String newString = null;
                newString = b.getString("Imageselected");
                mImageUri = Uri.parse(newString);
                ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MappingActivity.this).build();
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(config);
                imageLoader.loadImage(newString,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        //super.onLoadingComplete(imageUri, view, loadedImage);
                        imageToMap.setImage(ImageSource.bitmap(loadedImage));
                    }
                });
            }

        }




//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MappingActivity.this).build();
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(config);
//        imageLoader.loadImage(newString,new SimpleImageLoadingListener(){
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                //super.onLoadingComplete(imageUri, view, loadedImage);
//                imageToMap.setImage(ImageSource.bitmap(loadedImage));
//            }
//        });

        imageToMap.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener(){

                @Override
                public void onLongPress(MotionEvent e) {
                    float x = (float)Math.floor(e.getX()*100)/100;
                    float y = (float)Math.floor(e.getY()*100)/100;

                    pointX.setText(imageToMap.viewToSourceCoord(x,y).toString().substring(7,imageToMap.viewToSourceCoord(x,y).toString().indexOf(",")));
                    pointY.setText(imageToMap.viewToSourceCoord(x,y).toString().substring(imageToMap.viewToSourceCoord(x,y).toString().indexOf(",")+2,imageToMap.viewToSourceCoord(x,y).toString().length()-1));
                    Log.i("MAPPOSITION",imageToMap.viewToSourceCoord(x,y).toString());
                    super.onLongPress(e);
                }
            });


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });










        saveMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                final WifiManager wifiManager =
                        (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                registerReceiver(new BroadcastReceiver() {

                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        scanList = wifiManager.getScanResults();
                        Testing tester = new Testing(scanList, myMapper);
                        predictedCoord = tester.getPrediction(myMapper);
                        System.out.println(predictedCoord + "123456789");


                    }

                }, filter);


                boolean startScan = wifiManager.startScan();
                if(!startScan){
                    Toast.makeText(MappingActivity.this,"Please Enable Access of Location",Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }

            }
        });
        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWifiNetworksList();




            }
        });
//        buttonaddPoint = findViewById(R.id.addPoint);
//        pointX = findViewById(R.id.pointX);
//        pointY = findViewById(R.id.pointY);
//
//        listView = findViewById(R.id.wifiList);
//        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        if (!wifiManager.isWifiEnabled()) {
//            Toast.makeText(this, "Wifi is disabled", Toast.LENGTH_LONG).show();
//            wifiManager.setWifiEnabled(true);
//        }
//
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
//        listView.setAdapter(adapter);
//
//
//        scanWifi();
//
//        buttonaddPoint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mapper.add_data(new Point(Double.parseDouble(pointX.getText().toString()), Double.parseDouble(pointY.getText().toString())), results);
//                System.out.println("Data added!");
//            }
//        });

    }

//    private void scanWifi() {
//
//            // scan without any restrictions
//
//            arrayList.clear();
//            registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//
//            wifiManager.startScan();
//            Toast.makeText(this, "Scanning Wifi...", Toast.LENGTH_SHORT).show();
//
//
//
//    }
//
//    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            results = wifiManager.getScanResults();
//            unregisterReceiver(this);
//
//            for (ScanResult scanResult : results) {
//                arrayList.add(" BSSID: \n" + scanResult.BSSID);
//                adapter.notifyDataSetChanged();
//            }
//        }
//    };
private void getWifiNetworksList() {


    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    final WifiManager wifiManager =
            (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    registerReceiver(new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onReceive(Context context, Intent intent) {

            scanList = wifiManager.getScanResults();


            currentCoord = new Point(Double.parseDouble(pointX.getText().toString()), Double.parseDouble(pointY.getText().toString()));
            locationNameString = locationName.getText().toString();
            myMapper.add_data(currentCoord, scanList);

            Toast.makeText(getApplicationContext(), "Data Saved", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < scanList.size(); i++) {

                String Mac_address = scanList.get(i).BSSID;
                Integer rssi = scanList.get(i).level;
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild(Mac_address)){
                            database.child("Scan").child(locationNameString).child("MAC Address").child(Mac_address).setValue(rssi);
                            database.child("Scan").child(locationNameString).child("Coordinates").setValue(currentCoord);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }




        }

    }, filter);


    boolean startScan = wifiManager.startScan();
    if(!startScan){
        Toast.makeText(MappingActivity.this,"Please Enable Access of Location",Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(myIntent);
    }

}

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        SubsamplingScaleImageView imageView;
        URL url;
        public LoadImage(SubsamplingScaleImageView PreviewImage){
            this.imageView = PreviewImage;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URLlink = strings[0];
            Bitmap bitmap = null;
            try {
                if(!URLlink.isEmpty()){
                    url = new URL(URLlink);
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                //InputStream inputStream = new java.net.URL(URLlink).openStream();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //PreviewImage.setImageBitmap(bitmap);
            imageToMap.setImage(ImageSource.bitmap(bitmap));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            imageToMap.setImage(ImageSource.uri(mImageUri));


        }
    }


}