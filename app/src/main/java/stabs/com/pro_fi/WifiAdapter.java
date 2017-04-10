package stabs.com.pro_fi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter to manage the recycler view.
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private ArrayList<String> wifiNames;
    private boolean[] selectedWifis;

    public WifiAdapter(ArrayList<String> list) {
        wifiNames = list;
        selectedWifis = new boolean[list.size()];
    }

    @Override
    public WifiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WifiAdapter.ViewHolder holder, int position) {
        final String profileName = wifiNames.get(position);
        holder.wifiCard.setText(profileName);
        holder.itemView.setActivated(selectedWifis[position]);
        holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setActivated(!selectedWifis[holder.getAdapterPosition()]);
                        WifiAdapter.this.notifyItemChanged(holder.getAdapterPosition());
                        selectedWifis[holder.getAdapterPosition()] = !selectedWifis[holder.getAdapterPosition()];
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return wifiNames.size();
    }

    public ArrayList<String> getSelectedWifis() {
        ArrayList<String> result = new ArrayList<>();
        for (int i=0; i<selectedWifis.length; i++) {
            if (selectedWifis[i]) {
                result.add(wifiNames.get(i));
            }
        }
        return result;
    }

    public void setSelectedWifis(boolean[] b) {
        this.selectedWifis = b;
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
