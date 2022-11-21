package com.example.scanqrapp.ui.zone_details;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanqrapp.R;
import com.example.scanqrapp.models.SingleScannedRow;

import java.util.ArrayList;

public class ScannedQrAdapter extends RecyclerView.Adapter<ScannedQrAdapter.ViewHolder> {

    private final ArrayList<SingleScannedRow> zoneArray;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvUuid, productName;

        public ViewHolder(View view) {
            super(view);
            tvUuid = view.findViewById(R.id.tv_uuid);
            productName = view.findViewById(R.id.tv_product_name);
        }
    }

    public ScannedQrAdapter(ArrayList<SingleScannedRow> dataSet, Context context) {
        zoneArray = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_qr_scan, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.tvUuid.setText(zoneArray.get(position).uniqueId);
        viewHolder.productName.setText(
                String.format(
                        context.getResources().getString(R.string.zone_detail_unique_id),
                        zoneArray.get(position).building,
                        zoneArray.get(position).floor,
                        zoneArray.get(position).zone,
                        zoneArray.get(position).productName.substring(0, 4),
                        zoneArray.get(position).uuid.substring(0, 4)
                ));
        viewHolder.setIsRecyclable(false);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return zoneArray.size();
    }

}
