package stabs.com.pro_fi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout profileLayout;
    private TextInputEditText profileEditText;

    private Profile profile;
    private int profileId;
    private SeekBar ring,notif,media,sys;
    final int MAX_SEEK=15;
    float scale=1.4f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add0_activity_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileLayout = (TextInputLayout)findViewById(R.id.name_layout);
        profileEditText = (TextInputEditText)findViewById(R.id.profile_name);
        Button backButton = (Button) findViewById(R.id.back_button);

        ring = (SeekBar) findViewById(R.id.ringtone_seekbar);
        notif = (SeekBar) findViewById(R.id.notifications_seekbar);
        media = (SeekBar) findViewById(R.id.media_seekbar);
        sys = (SeekBar) findViewById(R.id.system_seekbar);
        SeekBar [] sound={ring,notif,media,sys};
        for (SeekBar aSound : sound) {
            aSound.setScaleY(scale);
            aSound.setScaleX(scale);
        }

        //Show all profile Settings
        //Show profile name
        profileId = getIntent().getIntExtra(Profile.ID, -1);

        profile = DBHelper.getInstance(this).getProfile(profileId);

        profileEditText.setText(profile.getName());
        ring.setProgress(profile.getRingtone());
        media.setProgress(profile.getMedia());
        notif.setProgress(profile.getNotification());
        sys.setProgress(profile.getSystem());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Add listeners to seekbars
        ring.setOnSeekBarChangeListener(new SeekListener(ring));ring.setMax(MAX_SEEK);
        media.setOnSeekBarChangeListener(new SeekListener(media)); media.setMax(MAX_SEEK);
        notif.setOnSeekBarChangeListener(new SeekListener(notif));notif.setMax(MAX_SEEK);
        sys.setOnSeekBarChangeListener(new SeekListener(sys));sys.setMax(MAX_SEEK);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void next(View v){
        DBHelper helper = DBHelper.getInstance(this);

        //Display toast if name is not entered
        String name = profileEditText.getText().toString().trim();
        if (name.isEmpty()) {
            profileLayout.setError(getString(R.string.enter_a_name));
            profileEditText.requestFocus();
        } else if(!helper.isUnique(DBHelper.PROFILE_NAME, name, profile.getId())) {
            profileLayout.setError(getString(R.string.name_exists_errr));
            profileEditText.requestFocus();
        } else {
            Intent myIntent = new Intent(this,EditWIFI.class);

            myIntent.putExtra(Profile.ID, profileId);
            startActivity(myIntent);
        }
    }

    // Listener class for seekbars
    class SeekListener implements SeekBar.OnSeekBarChangeListener{

        private SeekBar s;

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
