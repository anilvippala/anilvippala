package com.anilvippala.networkinfo;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.location.LocationManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
// This is the latest code


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Details of the texViews used

        TextView myText = findViewById(R.id.myText);
        TextView longitude = findViewById(R.id.myLongitude);
        TextView latitude = findViewById(R.id.myLatitude);
        Button myButton = findViewById(R.id.myButton);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        latitude.setText("Latitude of the device is " + String.valueOf(locationGPS.getLatitude()));
        longitude.setText("longitude of the device is " + String.valueOf(locationGPS.getLongitude()));

        //String mySignalStrength = getSignalStrength(getApplicationContext());
        //myText.setText("Signalling details of the device is " + mySignalStrength);
        String wifiSignal = getWifiInfo(getApplicationContext());
        myText.setText("Signalling details of the wifi connection  is " + wifiSignal);

    }

    public static String getSignalStrength(Context context) throws SecurityException {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mobileIMSI = telephonyManager.getSubscriberId();
        System.out.println("Imsi of the mobile is " + mobileIMSI);
        String strength = null;
        String getLTECellInfo1 = null;
        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();//This will give info of all sims present inside your mobile

        Log.d(TAG, "cell info is  " + cellInfos);
        if(cellInfos != null) {
            for (int i = 0 ; i < cellInfos.size() ; i++) {
                if (cellInfos.get(i).isRegistered()) {
                    if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = "Got GSM signal Strength" + String.valueOf(cellSignalStrengthGsm.getDbm());
                        System.out.println("location info for Anil-cellinfo gsm" );
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = "Got LTE signal Strength " + String.valueOf(cellSignalStrengthLte.getDbm());
                        System.out.println("location info for Anil " + cellInfoLTEJSON(cellInfoLte, true));
                        getLTECellInfo1 = cellInfoLTEJSON(cellInfoLte, true);
                    } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        strength = "Got CDMA signal Strength" + String.valueOf(cellSignalStrengthCdma.getDbm());
                        System.out.println("location info for Anil-cellinfo cdma" );
                    }
                }
            }
        }
        if (getLTECellInfo1 != null){
            return getLTECellInfo1;
        } else {
            return strength;
        }
    }


    public static String cellInfoLTEJSON(CellInfoLte cellInfo, boolean returnSignalStrength) {
        final Calendar calendar = Calendar.getInstance();
        final JSONObject json = new JSONObject();
        if (cellInfo != null) {
            try {
                //json.put("provider", "Jio");
                json.put("type", "LTE");
                json.put("timestamp", calendar.getTimeInMillis());
                final CellIdentityLte identityLte = cellInfo.getCellIdentity();
                json.put("ci", identityLte.getCi());
                json.put("mcc", identityLte.getMcc());
                json.put("mnc", identityLte.getMnc());
                json.put("pci", identityLte.getPci());
                json.put("tac", identityLte.getTac());
                json.put("earfcn", identityLte.getTac());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    json.put("bands", identityLte.getBands());
                    json.put("bw", identityLte.getBandwidth());
                }
                if (returnSignalStrength){
                    final JSONObject jsonSignalStrength = new JSONObject();
                    final CellSignalStrengthLte cellSignalStrengthLte = cellInfo.getCellSignalStrength();
                    jsonSignalStrength.put("asuLevel", cellSignalStrengthLte.getAsuLevel());
                    jsonSignalStrength.put("dbm", cellSignalStrengthLte.getDbm());
                    jsonSignalStrength.put("level", cellSignalStrengthLte.getLevel());
                    jsonSignalStrength.put("timingAdvance", cellSignalStrengthLte.getTimingAdvance());
                    jsonSignalStrength.put("cqi", cellSignalStrengthLte.getCqi());
                    jsonSignalStrength.put("rsrp", cellSignalStrengthLte.getRsrp());


                    json.put("cellSignalStrengthLte", jsonSignalStrength);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json.toString();
    }


    public static String getWifiInfo(Context context) {
        //String wifiInfo = null;
        WifiInfo connectionInfo = null;

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            connectionInfo = wifiManager.getConnectionInfo();
//            if (connectionInfo != null && !String.Util.isBlank(connectionInfo.getSSID())) {
//                ssid = connectionInfo.getSSID();
//            }
        }
        
        System.out.println("connection info is " + connectionInfo);
        return connectionInfo.toString();
    }

}