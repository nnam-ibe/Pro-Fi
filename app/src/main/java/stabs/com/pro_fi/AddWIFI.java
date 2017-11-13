package stabs.com.pro_fi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.Manifest;

public class AddWIFI extends AppCompatActivity {

    public static final String TAG = "AddWifi";

    private WifiAdapter wifiAdapter;
    WifiManager wifi;
    List<WifiConfiguration> configuredWifis;
    List<ScanResult> wifis, conf_wifis;
    List<String> names=new ArrayList <String>(); // NAMES OF WIFI
    RecyclerView recyclerView;
    Profile profile;
    final int REQUEST_PERMISSION_FINE_LOCATION = 1;
    BroadcastReceiver receiver;


    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: entered receive *************");

            // Get scan result.
            wifis = wifi.getScanResults();

            // Check if any pre configured network is in scan result.
            if (wifis.size() > 0 && configuredWifis != null) {

                conf_wifis = new ArrayList<ScanResult>();

                for (WifiConfiguration e : configuredWifis) {
                    String e_SSID = e.SSID.replace("\"", "");
                    for (ScanResult result : wifis) {

                        // If match found,
                        if (e_SSID.equals(result.SSID)) {

                            // Add to conf_wifis if not already in conf_wifi.
                            if (!conf_wifis.contains(result)) {

                                conf_wifis.add(result);
                            }
                        }
                    }
                }

                // Add other network names to names.
                for (WifiConfiguration w : configuredWifis) {
                    if (!in(w, conf_wifis)) {
                        names.add(w.SSID.replace("\"", ""));
                    }
                }

                //Sort scan result by signal strength.
                Collections.sort(conf_wifis, new Comparator<ScanResult>(){
                    @Override
                    public int compare(ScanResult res1, ScanResult res2) {
                        if (res1.level < res2.level) { return -1; }
                        else if (res1.level > res2.level) { return 1; }
                        else { return  0; }
                    }
                });

                // Add sorted result names to top of names.
                for (int i = conf_wifis.size()-1; i >= 0; --i) {
                    String ssid = conf_wifis.get(i).SSID;
                    if (!names.contains(ssid)) {
                        names.add(0, ssid);
                    }
                }

            }
        }

    }


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
                null,
                getIntent().getIntExtra(Profile.RINGTONE, 0),
                getIntent().getIntExtra(Profile.MEDIA, 0),
                getIntent().getIntExtra(Profile.NOTIFICATION, 0),
                getIntent().getIntExtra(Profile.SYSTEM, 0));


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        configuredWifis = wifi.getConfiguredNetworks();

        // Create and register receiver for wifi.startScan().
        receiver = new WifiReceiver();
        receiver.onReceive(this, getIntent());

        registerReceiver(receiver, new IntentFilter(wifi.SCAN_RESULTS_AVAILABLE_ACTION));

        // Populate recyclerview.
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
        DBHelper helper= DBHelper.getInstance(this);

        if ( wifiAdapter.getSelectedWifis() == null ) {
            Toast.makeText(this, "Select a Wi-Fi", Toast.LENGTH_SHORT).show();
        }

        boolean isUnique = true;
        for (String wifiName:wifiAdapter.getSelectedWifis()) {
            isUnique &= helper.isUnique(DBHelper.WIFI_NAME, wifiName, -1);
        }

        if(!isUnique) {
            Toast.makeText(this, "Wi-Fi already in use", Toast.LENGTH_SHORT).show();
        } else {
            //Write contents profileInfo to DB.
            helper.insertProfile(profile, wifiAdapter.getSelectedWifis());

            SharedPreferences sharedPrefs = this.getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
            boolean isAutomatic = sharedPrefs.getBoolean("AutomaticSelect", false);

            // Activate New Profile, Ask if they want profile active??
            if (isAutomatic) {
                NetworkService networkService = new NetworkService(this);
                networkService.checkConnection();
            }
            //Switch to Home screen
            Intent myIntent=new Intent(this,MainActivity.class);
            startActivity(myIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister receiver.
        unregisterReceiver(receiver);
    }

    public boolean in(WifiConfiguration w, List<ScanResult> scan) {
        boolean flag = false;

        for (ScanResult s : scan) {
            if (w.SSID.replace("\"", "").equals(s.SSID)) {
                flag = true;
            }
        }

        return flag;
    }
}
