package stabs.com.pro_fi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

public class add0_activity extends AppCompatActivity {
private SeekBar ring,notif,media,sys;
    float scale=1.4f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add0_activity_layout);
        ring = (SeekBar) findViewById(R.id.ring_seek);
        notif = (SeekBar) findViewById(R.id.notif_seek);
        media = (SeekBar) findViewById(R.id.Media_seek);
        sys = (SeekBar) findViewById(R.id.sys_seek);
        SeekBar [] sound={ring,notif,media,sys};
        for (int i = 0; i < sound.length; i++) {
          sound[i].setScaleY(scale);
          sound[i].setScaleX(scale);
        }
    }
    public void add1_method(View v){
        Intent myIntent=new Intent(this,add1_activity.class);
        startActivity(myIntent);
    }
}
