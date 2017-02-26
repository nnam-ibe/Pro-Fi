package stabs.com.pro_fi;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    private SeekBar ring,notif,media,sys;
    final int MAX_SEEK=15;
    float scale=1.4f;
    FloatingActionButton imb;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit0_activity_layout);
        initialise();

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

        //Add listeners to seekbars
        ring.setOnSeekBarChangeListener(new SeekListener(ring));ring.setMax(MAX_SEEK);
        media.setOnSeekBarChangeListener(new SeekListener(media)); media.setMax(MAX_SEEK);
        notif.setOnSeekBarChangeListener(new SeekListener(notif));notif.setMax(MAX_SEEK);
        sys.setOnSeekBarChangeListener(new SeekListener(sys));sys.setMax(MAX_SEEK);

        //Show all profile Settings
        //Show profile name
        et.setText(getIntent().getStringExtra("PROFILE_NAME"));

        //Show ringtone setting
        ring.setProgress(Integer.valueOf(getIntent().getStringExtra("PROFILE_RINGTONE")));

        //Show media setting
        media.setProgress(Integer.valueOf(getIntent().getStringExtra("PROFILE_MEDIA")));

        //Show notifications setting
        notif.setProgress(Integer.valueOf(getIntent().getStringExtra("PROFILE_NOTIFICATIONS")));

        //Show system setting
        sys.setProgress(Integer.valueOf(getIntent().getStringExtra("PROFILE_SYSTEM")));

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

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // Method for initializing variables
    public void initialise(){

        et=(EditText)findViewById(R.id.nameTxt);
        imb=(FloatingActionButton)findViewById(R.id.next); //Next button
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

    public void edit1_method(View v){

        //Display toast if name is not entered
        if
                (et.getText().length()<=0) Toast.makeText(this, "Please enter a name for the profile", Toast.LENGTH_SHORT).show();
        else{
            Intent myIntent=new Intent(this,EditWIFI.class);

            //Pass all info to next activity
            myIntent.putExtra("NAME_TXT_VAL", et.getText().toString());
            myIntent.putExtra("WIFI", getIntent().getStringExtra("PROFILE_WIFI"));
            myIntent.putExtra("RINGTONE", Integer.toString(ring.getProgress()));
            myIntent.putExtra("MEDIA", Integer.toString(media.getProgress()));
            myIntent.putExtra("NOTIFICATIONS", Integer.toString(notif.getProgress()));
            myIntent.putExtra("SYSTEM", Integer.toString(sys.getProgress()));
            startActivity(myIntent);
        }
    }
}
