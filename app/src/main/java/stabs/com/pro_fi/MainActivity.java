package stabs.com.pro_fi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = "MainActivity";
    ArrayList<Profile> list;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    private NetworkService networkService;
    MaterialTapTargetPrompt mFabPrompt;
    MaterialTapTargetPrompt mSwitchPrompt;
    MaterialTapTargetPrompt mSelProfilePrompt;
    private static final String COMPLETED_ONBOARDING_PREF_NAME = "Shlack";
    private static final String COMPLETED_ONBOARDING_FIRST_PROFILE = "Shlack2";
    RecyclerView.Adapter mAdapter;
    private SharedPreferences sharedPrefs;
    private static MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;

        SwitchCompat mainswitch = (SwitchCompat) findViewById(R.id.compatSwitch);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        list = DBHelper.getInstance(this).getAllProfiles();
        networkService = new NetworkService(this);
        boolean check;

        setUpDefaultProfileCard();
        View defaultProfileCard = findViewById(R.id.default_profile);
        defaultProfileCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                networkService.activateProfile(1,0);
                Toast.makeText(v.getContext(), "Default Profile Activated", Toast.LENGTH_SHORT).show();
                highlightActiveProfile();
                return true;
            }
        });

        final Button button = (Button) findViewById(R.id.default_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), button);
                popup.getMenuInflater().inflate(R.menu.default_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Edit")) {
                            Intent myIntent = new Intent(MainActivity.this, EditProfileActivity.class);
                            myIntent.putExtra(Profile.ID, 1);
                            MainActivity.this.startActivity(myIntent);
                        }
                        return true;
                    }
                });
                popup.setGravity(Gravity.RIGHT);
                popup.show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null)
        {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new ProfileAdapter(list, this);
            recyclerView.setAdapter(mAdapter);
        }
        sharedPrefs = getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        check = sharedPrefs.getBoolean("AutomaticSelect", false);
        highlightActiveProfile();

        mainswitch.setChecked(check);
        mainswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    networkService.checkConnection();
                    // The toggle is enabled
                    SharedPreferences.Editor editor = getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("AutomaticSelect", true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("AutomaticSelect", false);
                    editor.apply();

                    // The toggle is disabled
                }
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // if first launch of the app
        if (!sharedPreferences.getBoolean(MainActivity.COMPLETED_ONBOARDING_PREF_NAME, false)){
            fab.setEnabled(false);
            mainswitch.setEnabled(false);
            showFabPrompt();
        }

        //if first ever profile created
        if (!sharedPreferences.getBoolean(MainActivity.COMPLETED_ONBOARDING_FIRST_PROFILE, false))
            if (recyclerView.getAdapter().getItemCount() == 1) {
                showSelProfilePrompt(recyclerView);
            }
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
                                }
                            }
                        })
                .show();
    }

    // show main switch prompt
    public void showSwitchPrompt() {
        Log.i("ID_COMPATSWITCH",""+R.id.compatSwitch);
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
                                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                    mSwitchPrompt.finish();
                                    SharedPreferences.Editor sEditor =
                                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                                    sEditor.putBoolean(MainActivity.COMPLETED_ONBOARDING_PREF_NAME, true);
                                    sEditor.apply();
                                    enableButtons();
                                }
                            }
                        })
                .show();
    }

    // show how to activate a profile
    public void showSelProfilePrompt(final RecyclerView v) {

        //wait till profile card is drawn on v.
        v.post(new Runnable() {
            @Override
            public void run() {

                int firstProfile = ((LinearLayoutManager)v.getLayoutManager()).findFirstVisibleItemPosition();
                View child = v.getLayoutManager().findViewByPosition(firstProfile);
                ProfileAdapter.ViewHolder vh = (ProfileAdapter.ViewHolder)v.findViewHolderForItemId(v.getChildItemId(child));

                if (mSelProfilePrompt != null) {
                    return;
                }
                mSelProfilePrompt = new MaterialTapTargetPrompt.Builder(MainActivity.this)
                        .setTarget(vh.textView)
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
        });
    }

    public void addMethod(View v){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        Intent myIntent = new Intent(this,CreateProfile.class);
        startActivity(myIntent);
    }

    /**
     * Setup the card for the default profile
     */
    private void setUpDefaultProfileCard() {
        View defaultProfileCard = findViewById(R.id.default_profile);
        defaultProfileCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                networkService.activateProfile(1);
                Toast.makeText(v.getContext(), "Default Profile Activated", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final Button defaultMenuButton = (Button) findViewById(R.id.default_button);
        defaultMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), defaultMenuButton);
                popup.getMenuInflater().inflate(R.menu.default_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Edit")) {
                            Intent myIntent = new Intent(MainActivity.this, EditProfileActivity.class);
                            myIntent.putExtra(Profile.ID, 1);
                            MainActivity.this.startActivity(myIntent);
                        }
                        return true;
                    }
                });
                popup.setGravity(Gravity.RIGHT);
                popup.show();
            }
        });
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

    private void highlightActiveProfile() {
        int activeProfile = sharedPrefs.getInt(NetworkService.ACTIVE_PROFILE, -1);
        View defaultProfileCard = findViewById(R.id.default_profile);
        if ( activeProfile == 1 ) {
            defaultProfileCard.setActivated(true);
            mAdapter.notifyDataSetChanged();
        } else {
            defaultProfileCard.setActivated(false);
        }
    }

    private void killToast() {
        if (this.toast!= null) {
            this.toast.cancel();
        }
    }

    public static MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onPause() {
        killToast();
        super.onPause();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.w(TAG, key);
        if (key.equals(NetworkService.ACTIVE_PROFILE)) {
            highlightActiveProfile();
        }
    }
}
