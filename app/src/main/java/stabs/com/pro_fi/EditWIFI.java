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

/**
 * Created by ethan on 2017-02-25.
 */
public class EditWIFI extends AppCompatActivity{
    private static final String TAG = "EditWIFI";

    private WifiAdapter wifiAdapter;
    WifiManager wifi;
    List<WifiConfiguration> wifis;
    List<String> names=new ArrayList<String>(); // NAMES OF WIFI
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

        int profileId = getIntent().getIntExtra(Profile.ID, -1);
        DBHelper dbHelper = DBHelper.getInstance(this);
        profile = dbHelper.getProfile(profileId);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifis = wifi.getConfiguredNetworks();
        WifiConfiguration [] array = new WifiConfiguration[wifis.size()];
        wifis.toArray(array);

        for(int i=0;i<wifis.size();i++) {
            names.add(array[i].SSID.replace("\"", ""));
        }
        Collections.sort(names);

        ArrayList<String> selectedWifiNames = dbHelper.getProfileWifiList(profileId);
        boolean[] selectedWifis = new boolean[names.size()];
        for(String wifi: selectedWifiNames) {
            int index = names.indexOf(wifi);
            selectedWifis[index] = true;
        }


        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            wifiAdapter = new WifiAdapter((ArrayList<String>) names);
            wifiAdapter.setSelectedWifis(selectedWifis);
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
        DBHelper helper = DBHelper.getInstance(this);

        boolean isUnique = true;
        for (String wifiName:wifiAdapter.getSelectedWifis()) {
            isUnique &= helper.isUnique(DBHelper.WIFI_NAME, wifiName, profile.getId());
        }

        if (isUnique) {
            helper.updateProfile(profile, wifiAdapter.getSelectedWifis());
            Toast.makeText(this, profile.getName() + " updated", Toast.LENGTH_SHORT).show();

            //Switch to Home screen
            Intent myIntent=new Intent(this,MainActivity.class);
            startActivity(myIntent);
        } else {
            Toast.makeText(this, "WiFi name taken", Toast.LENGTH_SHORT).show();
        }
    }
}
