package stabs.com.pro_fi;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to manage the recycler view.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    //private Fragment mFragment;
    AudioManager myAudioManager;
    String TAG ="ProfileAdapter";
    public ArrayList<Profile> profileNames;
    public ProfileAdapter(ArrayList<Profile> list)
    {
        profileNames = list;
    }
    boolean []fade;


    public void delete_Diag(final View view, final int position) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this Profile?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //delete From database
                        DBHelper helper= DBHelper.getInstance(view.getContext());
                        helper.deleteProfile(profileNames.get(position));
                        // continue with delete
                        // delete from screen.
                        Profile toRemove = profileNames.get(position);
                        profileNames.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, 1); // update position


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View v;
        public TextView textView;
        public Button button1;
        public ViewHolder(View v)
        {
            super(v);
            this.v=v;
            int size=profileNames.size();
            fade= new boolean[size];
            for(int i=0;i<size;i++){fade[i]=false;}
            textView = (TextView)v.findViewById(R.id.name_text_view);
             button1 = (Button)v.findViewById(R.id.button);

        }
    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_card, parent, false);
        return new ViewHolder(v);
    }
    public int checkSelect()
    {
        for(int j=0;j<fade.length;j++)
        {
            if(fade[j]) return j;
        }
        return -1;
    }
    public void select(Profile profileName,View v,int pos)
    {Toast.makeText(v.getContext(), profileName.getName() + " Selected", Toast.LENGTH_SHORT).show();
        v.setAlpha((float) .65);
        fade[pos] = true;}
    public void deSelect(Profile profileName,View v,int pos)
    {
        Toast.makeText(v.getContext(), profileName.getName() + " Deselected", Toast.LENGTH_SHORT).show();
        v.setAlpha((float) 1);
        fade[pos]=false;
    }
    public Profile getProfile(){return null;}
    public void activateProfile(View v,Profile profile, boolean Vib)
    {
        // Boolean Vib is for vibration
        //TODO implement option for vibrate and silent
        int RING =profile.getRingtone();
        int NOTIF=profile.getNotification();
        int SYS  =profile.getSystem();
        int MEDIA=profile.getMedia();



        myAudioManager = (AudioManager)v.getContext().getSystemService(Context.AUDIO_SERVICE);
       // myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MEDIA, 0);
        myAudioManager.setStreamVolume(AudioManager.STREAM_RING,RING, 0);
        myAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,NOTIF, 0);
        myAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,SYS, 0);



        Log.e(TAG, "VALUES " + MEDIA +" "+ RING +" "+ NOTIF+" " + SYS);



    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.ViewHolder holder, final int position) {
        final Profile profileName = profileNames.get(position);
        holder.textView.setText(profileName.getName());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activateProfile(v,profileName,true);
                Toast.makeText(v.getContext(), profileName.getName() + " Activated", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(v.getContext(), holder.button1);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Delete")) {
                            delete_Diag(v, position);

                        }

                        else if (item.getTitle().equals("Edit")){
                            //edit_Profile(v, position);
                        }
//                            Toast.makeText(
//                                    v.getContext(),
//                                    "You Clicked : " + item.getTitle(),
//                                    Toast.LENGTH_SHORT
//                            ).show();
                        return true;
                    }
                });
                popup.setGravity(Gravity.RIGHT);

                popup.show(); //showing popup menu
            }
        });
    }

}
