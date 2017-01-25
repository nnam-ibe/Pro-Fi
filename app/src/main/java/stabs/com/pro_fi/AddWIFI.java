package stabs.com.pro_fi;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AddWIFI extends AppCompatActivity {

    public static final String TAG = "AddWifi";
    public static final String NAME="NAME_TXT_VAL";
    public static final String WIFI="WIFI";
    public static final String RING="RINGTONE";
    public static final String MEDIA="MEDIA";
    public static final String NOTIF="NOTIFICATIONS";
    public static final String SYS="SYSTEM";

    WifiManager wifi;
    List<WifiConfiguration> wifis;
    List<String> names=new ArrayList <String>(); // NAMES OF WIFI
    List<String> scannedNetworks=new ArrayList<String>(); //
    String[] profileInfo = new String[6]; // ALL PROFILE DETAILS AND SETTINGS
    TextView wifiTxt;
    RecyclerView recyclerView;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add1_activity_layout);
        initialise();

        //Store info in this order - name, wifi, ringtone, media, notifications, system into profileInfo
        profileInfo[0] = getIntent().getStringExtra(NAME);
        profileInfo[2] = getIntent().getStringExtra(RING);
        profileInfo[3] = getIntent().getStringExtra(MEDIA);
        profileInfo[4] = getIntent().getStringExtra(NOTIF);
        profileInfo[5] = getIntent().getStringExtra(SYS);

        wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifis=wifi.getConfiguredNetworks();
        WifiConfiguration [] array= new WifiConfiguration[wifis.size()];
        wifis.toArray(array);
        names.add("None");// Empty spot

        // Get List of ScanResults
       // List<ScanResult> wifiList = wifi.getScanResults();
        //names.add("Size "+wifiList.size());

        for(int i=0;i<wifis.size();i++)
        {
            names.add(array[i].SSID.replace("\"", ""));
        }
        Collections.sort(names);
        //TODO Move Strongest Connections to the top

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter mAdapter = new WifiAdapter((ArrayList<String>) names, wifiTxt);
            recyclerView.setAdapter(mAdapter);
        }

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        //ListView wifi_list = (ListView) findViewById(R.id.wifi_List);
        //wifi_list.setAdapter(adapter);
        //Spinner spin =(Spinner)findViewById(R.id.spinner);
        //ArrayAdapter<String> adapter= new ArrayAdapter<String>(
        //        this, android.R.layout.simple_spinner_dropdown_item, names);
       // spin.setAdapter(adapter);
    }

    public void saveProfile(View v){

        profileInfo[1] = wifiTxt.getText().toString();
        profile = new Profile(
                profileInfo[0],
                profileInfo[1],
                Integer.parseInt(profileInfo[2]),
                Integer.parseInt(profileInfo[3]),
                Integer.parseInt(profileInfo[4]),
                Integer.parseInt(profileInfo[5])
         );

        DBHelper helper= DBHelper.getInstance(this);
        helper.insertProfile(profile);
        Log.e(TAG, "Wifi name: " + profile.getRingtone());
        Log.e(TAG, "Profile name: " + profile.getMedia());
        Log.e(TAG, "Profile name: " + profile.getNotification());
        Log.e(TAG, "Wifi name: " + profile.getSystem());
        //Write contents profileInfo to DB.


        //Switch to Home screen
        Intent myIntent=new Intent(this,MainActivity.class);
        startActivity(myIntent);

    }

    public void initialise(){
        wifiTxt = (TextView) findViewById(R.id.wifiTxt);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
