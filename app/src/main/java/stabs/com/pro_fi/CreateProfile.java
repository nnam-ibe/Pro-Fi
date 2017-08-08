package stabs.com.pro_fi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class CreateProfile extends AppCompatActivity {

    private TextInputLayout profileLayout;
    private TextInputEditText profileEditText;
    private SeekBar ring,notif,media,sys;
    private final int MAX_SEEK=15;
    WifiManager wifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add0_activity_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        profileLayout = (TextInputLayout)findViewById(R.id.name_layout);
        profileEditText = (TextInputEditText)findViewById(R.id.profile_name);
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ring = (SeekBar) findViewById(R.id.ringtone_seekbar);
        notif = (SeekBar) findViewById(R.id.notifications_seekbar);
        media = (SeekBar) findViewById(R.id.media_seekbar);
        sys = (SeekBar) findViewById(R.id.system_seekbar);

        //Add listeners to seekbars
        ring.setOnSeekBarChangeListener(new SeekListener(ring));ring.setMax(MAX_SEEK); ring.setProgress(0);
        media.setOnSeekBarChangeListener(new SeekListener(media)); media.setMax(MAX_SEEK);media.setProgress(0);
        notif.setOnSeekBarChangeListener(new SeekListener(notif));notif.setMax(MAX_SEEK);notif.setProgress(0);
        sys.setOnSeekBarChangeListener(new SeekListener(sys));sys.setMax(MAX_SEEK);sys.setProgress(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void next(final View v) {
        boolean wifiEnabled = wifiManager.isWifiEnabled();

        DBHelper helper = DBHelper.getInstance(this);

        //Display toast if name is not entered
        String name = profileEditText.getText().toString().trim();
        if (name.isEmpty()) {
            profileLayout.setError(getString(R.string.enter_a_name));
            profileEditText.requestFocus();
        } else if(!helper.isUnique(DBHelper.PROFILE_NAME, name, -1)) {
            profileLayout.setError(getString(R.string.name_exists_errr));
            profileEditText.requestFocus();
        }
        else if(!wifiEnabled)
        {
            new AlertDialog.Builder(this)
                    .setMessage("Please Turn on WIFI")
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            //Turn on WIFI
//                            //wifiManager.setWifiEnabled(true);
//                            next(v);
//                        }
//                    })
//                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
            {
            Intent myIntent=new Intent(this,AddWIFI.class);

            //Pass all info
            myIntent.putExtra(Profile.NAME, name);
            myIntent.putExtra(Profile.RINGTONE, ring.getProgress());
            myIntent.putExtra(Profile.MEDIA, media.getProgress());
            myIntent.putExtra(Profile.NOTIFICATION, notif.getProgress());
            myIntent.putExtra(Profile.SYSTEM, sys.getProgress());
            startActivity(myIntent);
        }
    }

    // Listener class for seekbars
    class SeekListener implements SeekBar.OnSeekBarChangeListener{

        SeekBar s;

        public SeekListener(SeekBar seekBar){
            s = seekBar;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int val, boolean fromUser){
            s.setProgress(val);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar){}

        @Override
        public  void onStopTrackingTouch(SeekBar seekBar){}

    }
}
