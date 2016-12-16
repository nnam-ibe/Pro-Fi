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

public class add0_activity extends AppCompatActivity {
private SeekBar ring,notif,media,sys;
    float scale=1.4f;
    FloatingActionButton imb;
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add0_activity_layout);

        et=(EditText)findViewById(R.id.nameTxt);
        imb=(FloatingActionButton)findViewById(R.id.next); //Next button
        imb.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.disabledColor)));


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

        EditText nameTxt = (EditText) findViewById(R.id.nameTxt);

        //Display toast if name is not entered
        if
                (nameTxt.getText().length()<=0) Toast.makeText(this, "Please enter a name for the profile", Toast.LENGTH_SHORT).show();
        else{
            Intent myIntent=new Intent(this,add1_activity.class);
            startActivity(myIntent);
        }
    }
}
