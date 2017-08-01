package stabs.com.pro_fi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Service to enable/disable profiles.
 */
public class NetworkService {
    private static final String TAG = "NetworkService";
    public static final String ACTIVE_PROFILE = "ACTIVE_PROFILE";

    private Context context;
    private AudioManager myAudioManager;

    public NetworkService(Context context) {
        this.context = context;
        myAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Activates the profile with WiFi name wifiName
     * @param wifiName The name of the WiFi
     */
    public void activateProfile(String wifiName) {
        Log.w(TAG, "Retrieving profile " + wifiName);
        Profile profile = DBHelper.getInstance(context).getProfile(wifiName);
        activateProfile(profile);
    }

    /**
     * Activates the profile 'profile'
     * @param profile the profile to be activated
     */
    public void activateProfile(Profile profile) {
        if (profile == null) {
            Log.w(TAG, "No profile to connect to.");
            return;
        }

        if (!myAudioManager.isWiredHeadsetOn()) {
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, profile.getMedia(), 0);
            myAudioManager.setStreamVolume(AudioManager.STREAM_RING, profile.getRingtone(), 0);
            myAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, profile.getNotification(), 0);
            myAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, profile.getSystem(), 0);

            SharedPreferences.Editor editor = context.getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
            editor.putInt(ACTIVE_PROFILE, profile.getId());
            editor.apply();
        } else {
            Intent intent = new Intent(context, AudioService.class);
            context.startService(intent);
        }
    }
    /**
     * Checks if there are any Profiles to connect to after a change in network state
     * or mode (Automatic, Manual)
     */
    public void checkConnection() {
        String wifiName = null;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connec.getActiveNetworkInfo();
        if (wifi != null && wifi.isConnected() && wifi.getType() == ConnectivityManager.TYPE_WIFI) {
            final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !(connectionInfo.getSSID().equals(""))) {
                wifiName = connectionInfo.getSSID().replace("\"", "");
                Log.e(TAG, "Connected to " + wifiName);
            }

            activateProfile(wifiName);
        } else {
            Log.e(TAG, "No WiFi connection");
        }
    }

}
