package stabs.com.pro_fi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Adapter to manage the recycler view.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    //private Fragment mFragment;
    private static final String TAG ="ProfileAdapter";

    private NetworkService networkService;
    private SharedPreferences sharedPrefs;
    private Context context;
    public ArrayList<Profile> profileNames;

    public ProfileAdapter(ArrayList<Profile> list, Context context)
    {
        profileNames = list;
        networkService = new NetworkService(context);
        sharedPrefs = context.getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
        this.context = context;
    }

    public void delete_Diag(final View view, final int position) {
        new AlertDialog.Builder(view.getContext())
                .setMessage(context.getString(R.string.delete_profile))
                .setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
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
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
        final Profile profileName = profileNames.get(position);
        holder.textView.setText(profileName.getName());
        int activeProfile = sharedPrefs.getInt(NetworkService.ACTIVE_PROFILE, -1);
        holder.itemView.setActivated(profileName.getId() == activeProfile);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int activeProfile = sharedPrefs.getInt(NetworkService.ACTIVE_PROFILE, -1);
                networkService.activateProfile(profileName);
                Profile profile = DBHelper.getInstance(context).getProfile(activeProfile);
                int index = profileNames.indexOf(profile);
                ProfileAdapter.this.notifyItemChanged(index);
                ProfileAdapter.this.notifyItemChanged(holder.getAdapterPosition());
                MainActivity.getInstance().highlightActiveProfile();

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

                        } else if (item.getTitle().equals("Edit")) {
                            edit_Profile(v, position);
                        }
                        return true;
                    }
                });
                popup.setGravity(Gravity.RIGHT);

                popup.show(); //showing popup menu
            }
        });
    }

    //Switch to edit view
    public void edit_Profile(final View view, final int position){
        Intent myIntent = new Intent(view.getContext(), EditProfileActivity.class);
        myIntent.putExtra(Profile.ID, profileNames.get(position).getId());

        view.getContext().startActivity(myIntent);
    }

}
