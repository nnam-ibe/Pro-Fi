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
import java.util.HashMap;
import java.util.List;

public class add1_activity extends AppCompatActivity {

    WifiManager wifi;
    List<WifiConfiguration> wifis;
    List<String> names=new ArrayList <String>(); // NAMES OF WIFI
    String[] profileInfo = new String[6]; // ALL PROFILE DETAILS AND SETTINGS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add1_activity_layout);

        // SET VALUE OF EDITTEXT TO LAST ENTERED NAME
        TextView wifiTxt = (TextView) findViewById(R.id.wifiTxt);
        //Store info in this order - name, wifi, ringtone, media, notifications, system into profileInfo
        profileInfo[0] = getIntent().getStringExtra("NAME_TXT_VAL");
        profileInfo[2] = getIntent().getStringExtra("RINGTONE");
        profileInfo[3] = getIntent().getStringExtra("MEDIA");
        profileInfo[4] = getIntent().getStringExtra("NOTIFICATIONS");
        profileInfo[5] = getIntent().getStringExtra("SYSTEM");

        wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifis=wifi.getConfiguredNetworks();
        WifiConfiguration [] array= new WifiConfiguration[wifis.size()];
        wifis.toArray(array);
        names.add("");
        for(int i=0;i<wifis.size();i++)
        {
            names.add(array[i].SSID.replace("\"", ""));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
        TextView wifi = (TextView) findViewById(R.id.wifiTxt);

        profileInfo[1] = wifi.getText().toString();

        //Write contents profileInfo to DB.
        try{
            File file = new File(getFilesDir(), "MockDB.txt");
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            String profile = "";

            for (int i = 0; i < profileInfo.length; ++i){
                profile += profileInfo[i] + "_";
            }
            bw.write(profile);
            bw.newLine();
            bw.flush();
            bw.close(); fw.close();

        }catch (IOException e){
            e.printStackTrace();
        }

        //Switch to Home screen
        Intent myIntent=new Intent(this,MainActivity.class);
        startActivity(myIntent);

    }
}
