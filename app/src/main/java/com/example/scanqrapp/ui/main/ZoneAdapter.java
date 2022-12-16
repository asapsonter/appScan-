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
    private final MainFragmentCallbacks mainFragmentCallbacks;
    private int building, floor;
    private ArrayList<SingleScannedRow> excelRecordsList;


    /*this the class where the zone adapter in charge of pass data in the zone UI is stated */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final ImageView imageViewAdd;
        public final View bottomSeparator, rightSeparator;
        public final ConstraintLayout itemZoneMain;


        //initiating the viewholder for item_zones IDs.
        public ViewHolder( View view) {
            super(view);
            // here you state objects ids of all item_zone properties
            //

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
                       MainFragmentCallbacks mainFragmentCallbacks){
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //view inflate item_zone layout
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_zone, viewGroup,false);
        //return new view holder
        return new ViewHolder(view);
    }
     //set position and size position of the adapter items
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (position < zoneArray.size()){
            viewHolder.tvName.setText(String.format(context.getResources().getString(R.string.zone_with_id),zoneArray.get(position)));

            for (SingleScannedRow record : excelRecordsList) {
                if (record.building.equals(this.building + "") && record.floor.equals(this.floor + "") && record.zone.equals(zoneArray.get(position) + "")) {
                    //here is block that will make a zone text switch color to blue if it is occupied
                    viewHolder.tvName.setTextColor(context.getResources().getColor(R.color.app_blue, context.getTheme()));
                    break;
                } else {
                    //if not occupied set color black
                    viewHolder.tvName.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
                }
            }
        }
        // IF statement to add the number of grids in recycler view positions
        if (position >= (getItemCount() - 3)){
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
