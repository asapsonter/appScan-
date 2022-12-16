package com.example.scanqrapp.ui.zone_details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanqrapp.R;
import com.example.scanqrapp.models.SingleScannedRow;

import java.util.ArrayList;

public class ScannedQrAdapter extends RecyclerView.Adapter<ScannedQrAdapter.ViewHolder> {
    private final ArrayList<SingleScannedRow> zoneArray;
    private final Context context;
    private final DetailFragmentCallbacks detailFragmentCallbacks;

    public static  class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tvUuid, productName;
        public final ConstraintLayout root;
        public ViewHolder(View view) {
            super(view);
            tvUuid = view.findViewById(R.id.tv_uuid);
            productName = view.findViewById(R.id.tv_product_name);
            root = view.findViewById(R.id.root);
        }

    }
    public ScannedQrAdapter(ArrayList<SingleScannedRow> dataSet, Context context, DetailFragmentCallbacks detailFragmentCallbacks) {
        zoneArray = dataSet;
        this.context = context;
        this.detailFragmentCallbacks = detailFragmentCallbacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_qr_scan, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScannedQrAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.tvUuid.setText(zoneArray.get(position).uniqueId);
        viewHolder.productName.setText(zoneArray.get(position).productName);

        viewHolder.root.setOnLongClickListener(view -> {
            detailFragmentCallbacks.onItemLongPress(zoneArray.get(viewHolder.getAdapterPosition()));
            return false;
        });

        viewHolder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {

        return zoneArray.size();
    }
}
