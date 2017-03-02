package stabs.com.pro_fi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ethan on 2016-12-18.
 */

/**
 * Adapter to manage the recycler view.
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private ArrayList<String> profileNames;
    private int activeIndex;
    public String wifiName;

    public WifiAdapter(ArrayList<String> list)
    {
        profileNames = list;
        activeIndex = -1;
        wifiName = null;
    }

    @Override
    public WifiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WifiAdapter.ViewHolder holder, final int position) {
        final String profileName = profileNames.get(position);
        holder.wifiCard.setText(profileName);
        holder.itemView.setActivated(activeIndex == position);
        holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setActivated(true);
                        wifiName = profileNames.get(holder.getAdapterPosition());
                        WifiAdapter.this.notifyItemChanged(holder.getAdapterPosition());
                        if (activeIndex != -1) {
                            WifiAdapter.this.notifyItemChanged(activeIndex);
                        }
                        activeIndex = holder.getAdapterPosition();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return profileNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wifiCard;
        public View v;
        public ViewHolder(View v) {
            super(v);
            this.v = v;
            wifiCard = (TextView)v.findViewById(R.id.name_text_view);
        }
    }

}
