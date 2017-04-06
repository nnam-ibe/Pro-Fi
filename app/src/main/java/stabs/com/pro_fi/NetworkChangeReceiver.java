package stabs.com.pro_fi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by OGBA on 2017-01-29.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static NetworkChangeReceiver mInstance = null;
    public static boolean isWifiConnected = true;
    public static final String TAG = "NetworkChangeReceiver";
    public String wifiName;

    public static NetworkChangeReceiver getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkChangeReceiver();
        }
        return mInstance;
    }

    public boolean isConnected(){return isWifiConnected;}

//  TODO: onReceive gets called twice
    @Override
    public void onReceive( Context context, final Intent intent) {
        // Check if in manual mode or automatic
        SharedPreferences sharedPrefs = context.getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
        boolean isAutomatic = sharedPrefs.getBoolean("AutomaticSelect", false);

        // If in manual mode, nothing to do here, return.
        if (isAutomatic) {
            NetworkService networkService = new NetworkService(context);
            networkService.checkConnection();
        } else {
            Log.e(TAG, "In Manual Mode");
        }
    }

}