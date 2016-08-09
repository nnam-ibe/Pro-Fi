package stabs.com.pro_fi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    public static void delete_Diag(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this Profile?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
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
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProfileAdapter.ViewHolder holder, int position) {
        String profileName = profileNames.get(position);
        holder.textView.setText(profileName);
    }

    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.name_text_view);
            final Button button1 = (Button)v.findViewById(R.id.button);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(v.getContext(), button1);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Delete")){delete_Diag(v);}
                            Toast.makeText(
                                    v.getContext(),
                                    "You Clicked : " + item.getTitle(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return true;
                        }
                    });
                    popup.setGravity(Gravity.RIGHT);

                    popup.show(); //showing popup menu
                }
                });
        }
    }

}
