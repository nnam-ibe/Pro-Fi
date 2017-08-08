package stabs.com.pro_fi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static final String TAG="MainActivity";
    private boolean check;
    ArrayList<Profile> list;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    private NetworkService networkService;
    MaterialTapTargetPrompt mFabPrompt;
    MaterialTapTargetPrompt mSwitchPrompt;
    MaterialTapTargetPrompt mSelProfilePrompt;
    private static final String COMPLETED_ONBOARDING_PREF_NAME = "Shlack";
    private static final String COMPLETED_ONBOARDING_FIRST_PROFILE = "Shlack2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView switchstatus=(TextView) findViewById(R.id.switchStatus);
        SwitchCompat mainswitch = (SwitchCompat) findViewById(R.id.compatSwitch);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        list = DBHelper.getInstance(this).getAllProfiles();
        check=false;
        networkService = new NetworkService(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null)
        {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter mAdapter = new ProfileAdapter(list, this);
            recyclerView.setAdapter(mAdapter);
        }
        SharedPreferences sharedPrefs = getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
         check=sharedPrefs.getBoolean("AutomaticSelect", false);
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
                    networkService.checkConnection();

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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /*if (!sharedPreferences.getBoolean(MainActivity.COMPLETED_ONBOARDING_PREF_NAME, false)){

        }*/

        // if first launch of the app
        if (!sharedPreferences.getBoolean(MainActivity.COMPLETED_ONBOARDING_PREF_NAME, false)){
            fab.setEnabled(false);
            mainswitch.setEnabled(false);
            showFabPrompt();
        }

        // if first ever profile created
        if (!sharedPreferences.getBoolean(MainActivity.COMPLETED_ONBOARDING_FIRST_PROFILE, false))
            if (recyclerView.getAdapter().getItemCount() == 1)
                showSelProfilePrompt();

    }

    public void enableButtons(){
        SwitchCompat mainswitch = (SwitchCompat) findViewById(R.id.compatSwitch);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setEnabled(true);
        mainswitch.setEnabled(true);
    }

    // show fab prompt
    public void showFabPrompt() {
        if (mFabPrompt != null) {
            return;
        }
        mFabPrompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.fab))
                .setPrimaryText("Add a new profile")
                .setSecondaryText("Tap this add button to add a new profile.")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(
                        new MaterialTapTargetPrompt.PromptStateChangeListener() {
                            @Override
                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {

                                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                    mFabPrompt.finish();

                                    showSwitchPrompt();
                                } else if (state == MaterialTapTargetPrompt.STATE_DISMISSING) {
                                    //mFabPrompt = null;
                                }
                            }
                        })
                .show();
    }

    // show main switch prompt
    public void showSwitchPrompt() {
        if (mSwitchPrompt != null) {
            return;
        }
        mSwitchPrompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.compatSwitch))
                .setPrimaryText("Mode Switch")
                .setSecondaryText("Tap this switch to toggle between Automatic and Manual modes.")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(
                        new MaterialTapTargetPrompt.PromptStateChangeListener() {
                            @Override
                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                //count++;
                                //Log.w(TAG, "Count is " + count);
                                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                    mSwitchPrompt.finish();
                                    SharedPreferences.Editor sEditor =
                                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                    sEditor.putBoolean(MainActivity.COMPLETED_ONBOARDING_PREF_NAME, true);
                                    sEditor.apply();
                                    enableButtons();
                                } else if (state == MaterialTapTargetPrompt.STATE_DISMISSING) {
                                    //mFabPrompt = null;
                                }
                            }
                        })
                .show();
    }

    // show how to activate a profile
    public void showSelProfilePrompt() {
        if (mSelProfilePrompt != null) {
            return;
        }
        mSelProfilePrompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.recycler_view))
                .setPrimaryText("Activate a Profile")
                .setSecondaryText("Hold a profile to activate it.")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setPromptStateChangeListener(
                        new MaterialTapTargetPrompt.PromptStateChangeListener() {
                            @Override
                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {

                                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                    mSelProfilePrompt.finish();
                                    SharedPreferences.Editor sEditor =
                                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                    sEditor.putBoolean(MainActivity.COMPLETED_ONBOARDING_FIRST_PROFILE, true);
                                    sEditor.apply();
                                } else if (state == MaterialTapTargetPrompt.STATE_DISMISSING) {
                                    //mFabPrompt = null;
                                }
                            }
                        })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void add_method(View v){
        Intent myIntent=new Intent(this,CreateProfile.class);
        startActivity(myIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
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
