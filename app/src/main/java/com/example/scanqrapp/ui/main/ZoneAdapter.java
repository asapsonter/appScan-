package com.example.scanqrapp.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanqrapp.R;
import com.example.scanqrapp.models.SingleScannedRow;

import java.util.ArrayList;

public  class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ViewHolder>{
    private final ArrayList<Integer> zoneArray;
    private final Context context;
    private final MainFragmentsCallbacks mainFragmentCallbacks;
    private int building, floor;
    private ArrayList<SingleScannedRow> excelRecordsList;


    /*this the class where the zone adapter in charge of pass data in the zone UI is stated */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final RecyclerView rvZones;
        public final ImageView imageViewAdd;
        public final View bottomSeparator, rightSeparator;
        public final ConstraintLayout itemZoneMain;


        //initiating the viewholder for item_zones IDs.
        public ViewHolder( View view) {
            super(view);
            // here you state objects ids of all item_zone properties
            //
            rvZones = (RecyclerView) view.findViewById(R.id.rv_zones);
            tvName = (TextView) view.findViewById(R.id.tv_zone);
            bottomSeparator = (View) view.findViewById(R.id.seperator_bottom);
            rightSeparator = (View) view.findViewById(R.id.seperator_right);
            imageViewAdd = (ImageView) view.findViewById(R.id.iv_add);
            itemZoneMain = (ConstraintLayout) view.findViewById(R.id.item_zone_main);

        }
    }
    public ZoneAdapter(ArrayList<Integer> dataSet,
                       Context context,
                       int _building,
                       int _floor,
                       ArrayList<SingleScannedRow> excelRecordsList,
                       MainFragmentsCallbacks mainFragmentCallbacks){
        zoneArray = dataSet;
        this.context = context;
        this.mainFragmentCallbacks = mainFragmentCallbacks;
        building = _building;
        floor = _floor;
        this.excelRecordsList = excelRecordsList;

    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //view inflate item_zone layout
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_zone, viewGroup,false);
        //return new view holder
        return new ViewHolder(view);
    }
     //set position and size position of the adapter items
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (position < zoneArray.size()){
            viewHolder.tvName.setText(String.format(context.getResources().getString(R.string.zone_with_id),zoneArray.get(position)));


        }


        // IF statement to add the number of grids in recycler view positions
        if (position >= (getItemCount()-3)){
            viewHolder.bottomSeparator.setVisibility(View.GONE);
        } else {
            viewHolder.bottomSeparator.setVisibility(View.VISIBLE);
        }

        if ((position + 1) % 3 == 0) {
            viewHolder.rightSeparator.setVisibility(View.GONE);
        }else{
            viewHolder.rightSeparator.setVisibility(View.VISIBLE);

        }

        if (position == zoneArray.size()){
            viewHolder.imageViewAdd.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewAdd.setVisibility(View.GONE);
        }

        viewHolder.itemZoneMain.setOnClickListener(
                view -> mainFragmentCallbacks
                        .onZoneClick(viewHolder
                                .getAdapterPosition()
                        )
        );

        viewHolder.setIsRecyclable(false);


    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return(3 - (zoneArray.size() % 3)) + zoneArray.size();
    }
}
