package stabs.com.pro_fi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by OGBA on 2017-01-29.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {


    private static NetworkChangeReceiver mInstance = null;
    public static boolean isWifiConnected = true;
    public static final String TAG = "NETWORKCHANGERECEIVER";
    public String wifiName;
    public static NetworkChangeReceiver getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkChangeReceiver();
        }
        return mInstance;
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !(connectionInfo.getSSID().equals(""))) {
                wifiName=connectionInfo.getSSID().replace("\"", "");
            }
            isWifiConnected = true;
           Log.e(TAG, "We are Connected to "+wifiName);
        } else {
            Log.e(TAG, "We are not Connected to "+wifiName);
            isWifiConnected = false;
        }
    }
}