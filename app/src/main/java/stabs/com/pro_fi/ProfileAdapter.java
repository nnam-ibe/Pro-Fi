package stabs.com.pro_fi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    ProfileAdapter context=this;
    public ArrayList<String> profileNames;

    public ProfileAdapter(ArrayList<String> list)
    {
        profileNames = list;
    }

    public void delete_Diag(final View view, final int position) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this Profile?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with delete
                        // delete from screen.
                        String toRemove = profileNames.get(position);
                        profileNames.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, 1);

                        // delete from DB
                        try{

                            File file = new File(view.getContext().getFilesDir(), "MockDB.txt");
                            FileReader fr = new FileReader(file);
                            BufferedReader br = new BufferedReader(fr);
                            List<String> lines = new ArrayList<String>();
                            String s = "";

                            // Transfer other data from MockDB.txt to lines
                            while ((s = br.readLine()) != null){
                                if (!s.startsWith(toRemove)){
                                    lines.add(s);
                                }
                            }

                            FileWriter fw = new FileWriter(file, false);
                            BufferedWriter bw = new BufferedWriter(fw);

                            for (String profile: lines
                                 ) {
                                bw.write(profile);
                                bw.newLine();
                            }

                            bw.flush(); bw.close(); fw.close();

                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
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

    public void edit_Profile(final View view, final int position){
        //Get all info of profile refrenced by position and pass it to edit0_activity

        String toEdit = profileNames.get(position);

        //If not Home, Work and School, read from DB
        if (!toEdit.equals("Home") && !toEdit.equals("Work") && !toEdit.equals("School")){

            //Read from DB
            try{

                File file = new File(view.getContext().getFilesDir(), "MockDB.txt");
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String profileInfo = "";

                while ((profileInfo = br.readLine()) != null){
                    if (profileInfo.startsWith(toEdit)){
                        break;
                    }
                }

                String[] info = profileInfo.split("####");
                Intent myIntent = new Intent(view.getContext(), edit0_activity.class);
                myIntent.putExtra("PROFILE_NAME", info[0]);
                myIntent.putExtra("PROFILE_WIFI", info[1]);
                myIntent.putExtra("PROFILE_RINGTONE", info[2]);
                myIntent.putExtra("PROFILE_MEDIA", info[3]);
                myIntent.putExtra("PROFILE_NOTIFICATIONS", info[4]);
                myIntent.putExtra("PROFILE_SYSTEM", info[5]);
                view.getContext().startActivity(myIntent);

            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

        }

    }


    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Button button1;
        public ViewHolder(View v) {
            super(v);
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

    @Override
    public void onBindViewHolder(final ProfileAdapter.ViewHolder holder, final int position) {
        String profileName = profileNames.get(position);
        holder.textView.setText(profileName);

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
                            edit_Profile(v, position);
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
