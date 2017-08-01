package stabs.com.pro_fi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by OGBA on 2017-07-28.
 */

public class AudioReceiver extends BroadcastReceiver {
    public static final String TAG ="AudioReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkService networkService = new NetworkService(context);

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);

            switch (state) {
                case 0:
                    Log.d(TAG, "Headset is unplugged");
                    Log.w(TAG, "Attempting to stop service...");
                    Intent serviceIntent = new Intent(context, AudioService.class);
                    context.stopService(serviceIntent);
                    Log.w(TAG, "Service stopped");
                    SharedPreferences sharedPrefs = context.getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
                    boolean isAutomatic = sharedPrefs.getBoolean("AutomaticSelect", false);

                    // If in manual mode, nothing to do here, return.
                    if (isAutomatic) {
                        networkService.checkConnection();
                    }
                    break;
                case 1:
                    Log.d(TAG, "Headset is plugged");
                    break;
                default:
                    Log.d(TAG, "I have no idea what the headset state is");
            }
        }
    }


}
