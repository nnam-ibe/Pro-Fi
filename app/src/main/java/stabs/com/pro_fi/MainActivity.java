package stabs.com.pro_fi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private Switch myswitch;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView switchstatus=(TextView) findViewById(R.id.switchStatus);
        SwitchCompat mainswitch = (SwitchCompat) findViewById(R.id.compatSwitch);
        ArrayList<Profile> list = DBHelper.getInstance(this).getAllProfiles();



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter mAdapter = new ProfileAdapter(list);
            recyclerView.setAdapter(mAdapter);
        }
        SharedPreferences sharedPrefs = getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
        boolean check=sharedPrefs.getBoolean("AutomaticSelect", false);
        mainswitch.setChecked(check);
        if (check) {
            // The toggle is enabled
            switchstatus.setText("Mode:    Automatic");
        } else {
            switchstatus.setText("Mode:    Manual");
            // The toggle is disabled
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mainswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    switchstatus.setText("Mode:    Automatic");

                    SharedPreferences.Editor editor = getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("AutomaticSelect", true);
                    editor.commit();
                } else {
                    switchstatus.setText("Mode:    Manual");
                    SharedPreferences.Editor editor = getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("AutomaticSelect", false);
                    editor.commit();

                    // The toggle is disabled
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public void add_method(View v){
        Intent myIntent=new Intent(this,CreateProfile.class);
        startActivity(myIntent);
    }

    public void refreshConnection(View view) {
        //Something
    }
    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            this.backPressedToExitOnce = true;
            showToast("Press again to exit");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }

    }
    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }
    private void killToast() {
        if (this.toast!= null) {
            this.toast.cancel();
        }
    }
    @Override
    protected void onPause() {
        killToast();
        super.onPause();
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://stabs.com.pro_fi/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://stabs.com.pro_fi/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
