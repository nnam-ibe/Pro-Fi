package stabs.com.pro_fi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Arrays;

public class SummaryActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG ="SummaryActivity";
    public static final String IS_NEW_PROFILE ="IS_NEW_PROFILE";

    private boolean isNewProfile;
    private Profile profile;
    private ArrayList<String> selectedWifis;
    private static boolean seekbarDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isNewProfile = getIntent().getBooleanExtra(IS_NEW_PROFILE, true);
        retrieveProfile();

        TextInputEditText profileEditText = (TextInputEditText) findViewById(R.id.profile_name);
        profileEditText.setText(profile.getName());
        int maxSeek = 15;

        SeekBar ring = (SeekBar) findViewById(R.id.ringtone_seekbar);
        ring.setProgress(profile.getRingtone());
        ring.setMax(maxSeek);
        ring.setOnTouchListener(this);

        SeekBar notif = (SeekBar) findViewById(R.id.notifications_seekbar);
        notif.setProgress(profile.getNotification());
        notif.setMax(maxSeek);
        notif.setOnTouchListener(this);

        SeekBar media = (SeekBar) findViewById(R.id.media_seekbar);
        media.setProgress(profile.getMedia());
        media.setMax(maxSeek);
        media.setOnTouchListener(this);

        SeekBar sys = (SeekBar) findViewById(R.id.system_seekbar);
        sys.setProgress(profile.getSystem());
        sys.setMax(maxSeek);
        sys.setOnTouchListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selected_wifis_list);
        if (selectedWifis.size() == 0) {
            findViewById(R.id.no_wifi_selected).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            WifiAdapter wifiAdapter = new WifiAdapter(selectedWifis);
            boolean[] selectedArray = new boolean[selectedWifis.size()];
            Arrays.fill(selectedArray, true);
            wifiAdapter.setSelectedWifis(selectedArray);
            wifiAdapter.setClickIsDisabled(true);
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

    private void retrieveProfile() {
        profile = new Profile(
                getIntent().getIntExtra(Profile.ID, -1),
                getIntent().getStringExtra(Profile.NAME),
                null,
                getIntent().getIntExtra(Profile.RINGTONE, 0),
                getIntent().getIntExtra(Profile.MEDIA, 0),
                getIntent().getIntExtra(Profile.NOTIFICATION, 0),
                getIntent().getIntExtra(Profile.SYSTEM, 0));

        selectedWifis = getIntent().getStringArrayListExtra(Profile.WIFI);
        Log.w(TAG, "There are " +  selectedWifis.size() + " wifis selected.");
    }

    public void back(View view) {
        onBackPressed();
    }

    public void saveProfile(View v) {
        DBHelper helper = DBHelper.getInstance(this);

        if (isNewProfile) {
            helper.insertProfile(profile, selectedWifis);
        } else {
            helper.updateProfile(profile, selectedWifis);
        }

        SharedPreferences sharedPrefs = this.getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
        boolean isAutomatic = sharedPrefs.getBoolean("AutomaticSelect", false);

        if (isAutomatic) {
            NetworkService networkService = new NetworkService(this);
            networkService.checkConnection();
        }

        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
