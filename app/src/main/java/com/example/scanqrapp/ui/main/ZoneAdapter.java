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

import java.io.File;
import java.util.ArrayList;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ViewHolder> {
    private final ArrayList<Integer> zoneArray;
    private final Context context;
    private final MainFragmentCallbacks mainFragmentCallbacks;
    private int building, floor;




    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvName;
        public final ImageView imageViewAdd;
        public final View bottomSeparator, rightSeparator;
        public final ConstraintLayout itemZoneMain;

        public ViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tv_zone);
            imageViewAdd = (ImageView) view.findViewById(R.id.iv_add);
            bottomSeparator = (View) view.findViewById(R.id.seperator_bottom);
            rightSeparator = (View) view.findViewById(R.id.seperator_right);
            itemZoneMain = (ConstraintLayout) view.findViewById(R.id.item_zone_main);

        }

    }

    public ZoneAdapter(ArrayList<Integer> dataSet , Context context, int building, int floor, MainFragmentCallbacks mainFragmentCallbacks) {
        zoneArray = dataSet;
        this.context = context;
        this.mainFragmentCallbacks = mainFragmentCallbacks;
        //building = _building;
        //floor = _floor;
    }


// recycler view methods ,
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // create a view which defines UI
        View view = LayoutInflater.from(viewGroup.getContext()).
                //inflate item_zone.xml layout
                inflate(R.layout.item_zone,viewGroup, false);

        //return
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        // set recycler text view
        if(position < zoneArray.size()){
            viewHolder.tvName.setText(
                    String.format(context.getResources().
                            getString(com.example.scanqrapp.R.string.zone_with_id),
                            zoneArray.get(position)
                    )
            );
            File file = new File(
                    context.getExternalFilesDir(null),
                    "building" + building + "_floor" + floor + "_zone" + zoneArray.get(position) + ".xls"
            );
            if (file.exists()) {
                viewHolder.tvName.setTextColor(context.getResources().getColor(R.color.app_blue, context.getTheme()));
            } else {
                viewHolder.tvName.setTextColor(context.getResources().getColor(R.color.black, context.getTheme()));
            }

        }

        if(position >= (getItemCount() - 3)){
            viewHolder.bottomSeparator.setVisibility(View.GONE);

        }else {
            viewHolder.bottomSeparator.setVisibility(View.VISIBLE);
        }

        if ((position + 1) % 3 == 0) {
            viewHolder.rightSeparator.setVisibility(View.GONE);
        } else {
            viewHolder.rightSeparator.setVisibility(View.VISIBLE);
        }

        if (position == zoneArray.size()) {
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
        return (3 - (zoneArray.size() % 3)) + zoneArray.size();

    }
}
