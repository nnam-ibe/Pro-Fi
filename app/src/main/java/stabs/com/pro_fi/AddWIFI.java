package stabs.com.pro_fi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddWIFI extends AppCompatActivity {

    public static final String TAG = "AddWifi";

    private WifiAdapter wifiAdapter;
    WifiManager wifi;
    List<WifiConfiguration> wifis;
    List<String> names=new ArrayList <String>(); // NAMES OF WIFI
    RecyclerView recyclerView;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add1_activity_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profile = new Profile(
                getIntent().getStringExtra(Profile.NAME),
                "",
                getIntent().getIntExtra(Profile.RINGTONE, 0),
                getIntent().getIntExtra(Profile.MEDIA, 0),
                getIntent().getIntExtra(Profile.NOTIFICATION, 0),
                getIntent().getIntExtra(Profile.SYSTEM, 0));

        wifi=(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifis=wifi.getConfiguredNetworks();
        WifiConfiguration [] array= new WifiConfiguration[wifis.size()];
        wifis.toArray(array);

        for(int i=0;i<wifis.size();i++)
        {
            names.add(array[i].SSID.replace("\"", ""));
        }
        Collections.sort(names);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            wifiAdapter = new WifiAdapter((ArrayList<String>) names);
            recyclerView.setAdapter(wifiAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveProfile(View v){
        profile.setWifi(wifiAdapter.getWifiName());
        DBHelper helper= DBHelper.getInstance(this);

        if ( profile.getWifi() == null ) {
            Toast.makeText(this, "Select a Wi-Fi", Toast.LENGTH_SHORT).show();
        } else if(!helper.isUnique(DBHelper.WIFI_NAME, profile.getWifi())) {
            Toast.makeText(this, "Wi-Fi already in use", Toast.LENGTH_SHORT).show();
        } else {
            //Write contents profileInfo to DB.
            helper.insertProfile(profile);

            //Switch to Home screen
            Intent myIntent=new Intent(this,MainActivity.class);
            startActivity(myIntent);
        }
    }
}
