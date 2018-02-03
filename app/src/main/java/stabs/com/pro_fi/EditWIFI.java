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
        profile = new Profile(
                profileId,
                getIntent().getStringExtra(Profile.NAME),
                null,
                getIntent().getIntExtra(Profile.RINGTONE, 0),
                getIntent().getIntExtra(Profile.MEDIA, 0),
                getIntent().getIntExtra(Profile.NOTIFICATION, 0),
                getIntent().getIntExtra(Profile.SYSTEM, 0));

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifis = wifi.getConfiguredNetworks();
        WifiConfiguration [] array = new WifiConfiguration[wifis.size()];
        wifis.toArray(array);

        for(int i=0;i<wifis.size();i++) {
            if(!array[i].SSID.equals(wifi.getConnectionInfo().getSSID())){
                names.add(array[i].SSID.replace("\"", ""));
            }
        }
        Collections.sort(names);
        names.add(0, wifi.getConnectionInfo().getSSID().replace("\"", ""));

        ArrayList<String> selectedWifiNames = DBHelper.getInstance(this).getProfileWifiList(profileId);
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

    public void showOverview(View v) {
        DBHelper helper = DBHelper.getInstance(this);

        if ( wifiAdapter.getSelectedWifis() == null ) {
            Toast.makeText(this, "Select a Wi-Fi", Toast.LENGTH_SHORT).show();
        }

        boolean isUnique = true;
        for (String wifiName : wifiAdapter.getSelectedWifis()) {
            isUnique &= helper.isUnique(DBHelper.WIFI_NAME, wifiName, profile.getId());
        }

        if(!isUnique) {
            Toast.makeText(this, "Wi-Fi already in use", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent myIntent = new Intent(this, SummaryActivity.class);
        myIntent.putExtra(Profile.ID, profile.getId());
        myIntent.putExtra(Profile.NAME, profile.getName());
        myIntent.putExtra(Profile.RINGTONE, profile.getRingtone());
        myIntent.putExtra(Profile.MEDIA, profile.getMedia());
        myIntent.putExtra(Profile.NOTIFICATION, profile.getNotification());
        myIntent.putExtra(Profile.SYSTEM, profile.getSystem());
        myIntent.putExtra(Profile.WIFI, wifiAdapter.getSelectedWifis());
        myIntent.putExtra(SummaryActivity.IS_NEW_PROFILE, false);
        startActivity(myIntent);
    }
}
