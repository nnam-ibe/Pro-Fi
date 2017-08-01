package stabs.com.pro_fi;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


public class AudioService extends Service {

    public static final String TAG = "AudioService";
    private static AudioService audioService;
    private static AudioReceiver audioReceiver;

    public AudioService() {
        audioService = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "Service started");
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(getAudioReceiver(), filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        if (audioReceiver == null) {
            Log.w(TAG, "AudioReceiver is null");
        } else {
            unregisterReceiver(getAudioReceiver());
        }
        return super.stopService(name);
    }

    private static AudioReceiver getAudioReceiver() {
        if (audioReceiver == null) {
            audioReceiver = new AudioReceiver();
        }
        return audioReceiver;
    }
}
