package stabs.com.pro_fi;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.graphics.Color;
import android.widget.Toast;

public class CreateProfile extends AppCompatActivity {

    private SeekBar ring,notif,media,sys;
    float scale=1.4f;
    FloatingActionButton imb;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add0_activity_layout);
        initialise();

        //Add listener to et
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                //Disable next button if name is not entered by just changing the color.
                if (s.toString().equals("")) {
                    imb.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disabledColor)));

                } else {
                    imb.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        //Add listeners to seekbars
        ring.setOnSeekBarChangeListener(new SeekListener(ring));
        media.setOnSeekBarChangeListener(new SeekListener(media));
        notif.setOnSeekBarChangeListener(new SeekListener(notif));
        sys.setOnSeekBarChangeListener(new SeekListener(sys));
    }

    public void add1_method(View v){

        //Display toast if name is not entered
        if
                (et.getText().length()<=0) Toast.makeText(this, "Please enter a name for the profile", Toast.LENGTH_SHORT).show();
        else{
            Intent myIntent=new Intent(this,add1_activity.class);

            //Pass all info
            myIntent.putExtra("NAME_TXT_VAL", et.getText().toString());
            myIntent.putExtra("RINGTONE", Integer.toString(ring.getProgress()));
            myIntent.putExtra("MEDIA", Integer.toString(media.getProgress()));
            myIntent.putExtra("NOTIFICATIONS", Integer.toString(notif.getProgress()));
            myIntent.putExtra("SYSTEM", Integer.toString(sys.getProgress()));
            startActivity(myIntent);
        }
    }

    // Method for initializing variables
    public void initialise(){

        et=(EditText)findViewById(R.id.nameTxt);
        imb=(FloatingActionButton)findViewById(R.id.next); //Next button
        imb.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disabledColor)));
        ring = (SeekBar) findViewById(R.id.ringtone_seekbar);
        notif = (SeekBar) findViewById(R.id.notifications_seekbar);
        media = (SeekBar) findViewById(R.id.media_seekbar);
        sys = (SeekBar) findViewById(R.id.system_seekbar);
        SeekBar [] sound={ring,notif,media,sys};
        for (int i = 0; i < sound.length; i++) {
            sound[i].setScaleY(scale);
            sound[i].setScaleX(scale);
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
