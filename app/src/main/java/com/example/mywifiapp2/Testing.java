package com.example.mywifiapp2;

import android.net.wifi.ScanResult;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 1. Receive wifi data about current location
 * 2. Get appropriate data set from database (wifi data and position)
 * 3. Predict position of current location (two methods: nn model, K nearest)*/
public class Testing {

    double[] x;

    // initialise hashmap and list of bssid
    private HashMap<String,Integer> bssid_rssi;
    private List<String> bssid;


    public Testing(List<ScanResult> scanResults, Mapping mapper){
        x = new double[mapper.getAp_list().size()];
        bssid_rssi = new HashMap<>();
        bssid = new ArrayList<>();
        // from the scanResults obtained, generate hashmap and List
        for(ScanResult ap: scanResults){
            if(20<Math.abs(ap.level)&&Math.abs(ap.level)<100){
                bssid_rssi.put(ap.BSSID, ap.level);
                bssid.add(ap.BSSID);
            }
        }
    }

    public Point getPrediction(Mapping mapper){
        // train everytime we call getPrediction()?
        NeuralNetwork nn = mapper.train_data(bssid);
        List<Double> output = nn.predict(x);
        // return user location
        return new Point(output.get(0),output.get(1));
    }

    /**
     * K nearest method:
     * 1. Calculate dev = sqrt((rss1-rss1')^2+(rss2-rss2')^2+......) for every position in the data set
     * 2. Compare dev, find the smallest and the second smallest and their positions
     * 3. Make prediction of current positions based on the positions found with smallest and second smallest dev*/
//    public Point predict(List<String> bssid){
//        HashMap<Point,HashMap> dataSet = Mapping.get_data_for_testing(bssid);
//        ArrayList<Point> positionSet = new ArrayList<Point>(dataSet.keySet());
//        int num_of_positions = dataSet.size();
//        int num_of_bssids = bssid.size();
//
//        float nearest1 = Float.MAX_VALUE;
//        float nearest2 = Float.MAX_VALUE;
//
//        Point nearest1_position = new Point(0,0);
//        Point nearest2_position = new Point(0,0);
//
//        int sum = 0;
//        for(int i=0;i<num_of_positions;i++){
//            for(int j = 0; j< num_of_bssids; j++){
//                sum += Math.pow((int)dataSet.get(positionSet.get(i)).get(bssid.get(i))- bssid_rssi.get(bssid.get(j)),2);
//            }
//            float dev = (float) Math.sqrt(sum);
//            if(dev<nearest1){
//                nearest1 = dev;
//                nearest1_position = positionSet.get(i);
//            } else if(dev<nearest2){
//                nearest2 = dev;
//                nearest2_position = positionSet.get(i);
//            }
//            sum = 0;
//        }
//
//        double x = nearest1_position.getX()*nearest1/(nearest1+nearest2)+
//                nearest2_position.getX()*nearest2/(nearest1+nearest2);
//        double y = nearest1_position.getY()*nearest1/(nearest1+nearest2)+
//                nearest2_position.getY()*nearest2/(nearest1+nearest2);
//
//        return new Point(x,y);
//    }
}