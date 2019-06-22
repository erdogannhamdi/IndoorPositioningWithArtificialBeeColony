package com.example.hamdi.eddystone;

import android.Manifest;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.HttpGet;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.DistanceCalculator;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity implements BeaconConsumer  {

    private Button startButton;
    private Button stopButton;
    private double d;
    TextView t1;
    private double[] r = new double[8];
    private String[] isim = new String[8];
    private BeaconManager beaconManager = null;
    private Region beaconRegion = null;
    public static double Xk_kalmanEskiTahmin = 0.0;
    public static double Pk_eskiHataKovaryansi = 1; //hatanın ortak değişme miktarı,1
    public static double R_hataMiktari = 0.1; //0.1
    public static double Q = 0.0035; //0.0075---0.001------- en son 0.0035
    public double[] averageR = new double[8];
    public double[] sayac = new double[8];
    public int saniye = 0;
    public static String xy;
    private ListAdapter mAdapter;
    private List<Map<String, Object>> mList;
    private ListView mListView;
    public String url = "192.168.1.44";
    public Boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG,"onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        resetle();
        InitView();
        startButton = (Button) findViewById(R.id.start);
        stopButton = (Button) findViewById(R.id.stop);
        t1 = (TextView) findViewById(R.id.textX);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBeaconMonitoring();

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMonitoring();
                //Intent i = new Intent(MainActivity.this,Ploty.class);
                //startActivity(i);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
        }
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.setForegroundScanPeriod(100l); // 1000 mS 1000l
        beaconManager.setForegroundBetweenScanPeriod(0l); // 0ms
        beaconManager.bind(this);
    }

    private void startBeaconMonitoring(){
        try {
            beaconRegion = new Region("MyBeacons", null,null,null);
            beaconManager.startMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopMonitoring(){
        beaconManager.unbind(this);
        //Toast.makeText(getApplicationContext(), "Veri toplama işlemi tamamlandı. Simülatore bakınız.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
            }
            @Override
            public void didExitRegion(Region region) {
            }
            @Override
            public void didDetermineStateForRegion(int i, Region region) {
            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons != null && !beacons.isEmpty()){
                    saniye++;
                    //Log.i("cihazlar", "" + beacons.size());
                    for (Beacon beacon: beacons){
                        Xk_kalmanEskiTahmin = beacon.getRssi();
                        Pk_eskiHataKovaryansi = 0;
                        beacon.setHardwareEqualityEnforced(true);
                        int modifyIndex = -1;
                        for (int i = 0; i < mList.size(); i++){
                            if (mList.get(i).get("name").equals(beacon.getBluetoothName())){
                                modifyIndex = i;
                                Xk_kalmanEskiTahmin =Double.parseDouble((String)mList.get(i).get("eskiTahmin"));
                                Pk_eskiHataKovaryansi =Double.parseDouble((String)mList.get(i).get("hataKovaryansi"));
                                break;
                            }
                        }

                        double filteredRssi = KalmanHesapla(beacon.getRssi(),R_hataMiktari);
                        d =  Math.pow(10,(double)(beacon.getTxPower()-(int)filteredRssi)/23.0);    //evde 33 okulda 24.25

                        if (modifyIndex == -1) {
                            mList.add(getScanMap(beacon, (int)filteredRssi, d));
                            ((BaseAdapter) mAdapter).notifyDataSetChanged();

                        } else {
                            mList.set(modifyIndex, getScanMap(beacon, (int)filteredRssi, d));
                            ((BaseAdapter) mAdapter).notifyDataSetChanged();

                        }

                        for (int i = 0; i < mList.size(); i++){
                            for (int j = 1; j <= 8; j++){
                                if (mList.get(i).get("name").equals("Beacon" + j)) {
                                    averageR[j-1] += Double.parseDouble((String)mList.get(i).get("dist"));
                                    sayac[j-1] += 1;
                                }
                            }
                        }
                        if (saniye > 20){
                            check = true;
                        }
                        if (saniye >= 5 && check){

                            if (mList.size() >= 3){
                                for (int i = 0; i < mList.size(); i++) {
                                    for (int j = 1; j <= 8; j++){
                                        if (mList.get(i).get("name").equals("Beacon" + j)) {
                                            r[j-1] = averageR[j-1] / sayac[j-1];
                                            break;
                                        }
                                    }
                                }
                                sirala();
                                new SendFeedBack().execute();
                            }
                            saniye = 0;
                            resetle();
                        }
                        /*if (mList.size() >= 3){
                            sirala();
                            new SendFeedBack().execute();
                        }*/

                    }
                }
            }
        });
    }

    private HashMap<String, Object> getScanMap(Beacon device, int rssi, double distance) {

        HashMap<String, Object> map = new HashMap<String, Object>();

        if (device.getBluetoothName() == null) {
            map.put("name", "Unknown");
        } else {
            map.put("name", ""+device.getBluetoothName());
        }
        map.put("tx", "" + device.getTxPower());
        map.put("rssi", "" + rssi);
        if((-1)*rssi >=0 && (-1)*rssi < 55){
            map.put("image", R.mipmap.icon_rssi_3);
        } else if ((-1)*rssi >= 55 && (-1)*rssi < 70){
            map.put("image", R.mipmap.icon_rssi_2);
        } else if ((-1)*rssi >= 70){
            map.put("image", R.mipmap.icon_rssi_1);
        }
        map.put("unFiltredRssi", "" + device.getRssi());
        map.put("dist", "" + distance);
        map.put("eskiTahmin", "" + Xk_kalmanEskiTahmin);
        map.put("hataKovaryansi","" + Pk_eskiHataKovaryansi);

        return map;
    }

    private void InitView() {

        mListView = (ListView) findViewById(R.id.list);
        mList = new ArrayList<Map<String, Object>>();

        mAdapter = new SimpleAdapter(this, mList, R.layout.satir, new String[] { "name", "tx", "rssi", "image", "unFiltredRssi",
        "dist", "eskiTahmin", "hataKovaryansi"},
                new int[] { R.id.idName, R.id.idTx, R.id.idRssi , R.id.rssi_image, R.id.idUnfilteredRssi, R.id.idDist,
                        R.id.eskiTahmin, R.id.hataKovaryansi});

        mListView.setAdapter(mAdapter);

    }

    public static double KalmanHesapla(double olcum, double hata){
        //eski değerler yeni değerlere güncelleniyor
        double Xk_kalmanYeniTahmin = Xk_kalmanEskiTahmin;
        double Pk_yeniHataKovaryansi = Pk_eskiHataKovaryansi + Q;

        //ölçümleri düzeltme
        double Kk_kalmanKazanci = Pk_yeniHataKovaryansi / (Pk_yeniHataKovaryansi + hata);

        double Xk_kalmanHesabi = Xk_kalmanYeniTahmin + Kk_kalmanKazanci * (olcum - Xk_kalmanYeniTahmin);
        Pk_yeniHataKovaryansi = (1 - Kk_kalmanKazanci) * Pk_eskiHataKovaryansi;

        //eski değerleri atama
        Pk_eskiHataKovaryansi = Pk_yeniHataKovaryansi;
        Xk_kalmanEskiTahmin = Xk_kalmanHesabi;
        //bulunan sonuç bir sonraki adım için eski tahmin olacak

        return Xk_kalmanHesabi;
    }

    private class SendFeedBack extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            String output = null;

            URIBuilder builder = null;
            try {
                builder = new URIBuilder("http://" + url + ":8080/ABC2D/Server"); //10.25.240.42
                builder.setParameter("r1", "" + r[0] + "-" + isim[0]);
                builder.setParameter("r2", "" + r[1] + "-" + isim[1]);
                builder.setParameter("r3", "" + r[2] + "-" + isim[2]);
                //builder.setParameter("r4", "" + r[3] + "-" + isim[3]);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(builder.build());
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return output;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                t1.setText(s);

            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),e + "Server Error try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void resetle(){
        for (int i = 0; i < 8; i++){
            averageR[i] = 0;
            sayac[i] = 0;
        }
    }

    public void sirala(){
        double[] dizi = new double[mList.size()];
        String[] name = new String[mList.size()];
        double temp;
        String ad;
        for (int i = 0 ; i < mList.size(); i++){
            dizi[i] = Double.parseDouble((String)mList.get(i).get("dist"));
            name[i] = (String)mList.get(i).get("name");
        }

        for (int i = 0 ; i < dizi.length-1; i++) {
            for (int j = i+1; j < dizi.length; j++) {
                if (dizi[i] > dizi[j]) {
                    temp = dizi[i];
                    ad = name[i];
                    dizi[i] = dizi[j];
                    name[i] = name[j];
                    dizi[j] = temp;
                    name[j] = ad;
                }
            }
        }


        r[0] = dizi[0];
        r[1] = dizi[1];
        r[2] = dizi[2];

        isim[0] = name[0];
        isim[1] = name[1];
        isim[2] = name[2];

        //Log.i("isimler", isim[0] + " " + r[0] + " " + isim[1] + " " + r[1] + " " +isim[2] + " " + r[2]);

    }


}
