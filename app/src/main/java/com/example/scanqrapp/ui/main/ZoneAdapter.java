package com.example.scanqrapp.ui.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanqrapp.R;

public abstract class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final ImageView imageViewAdd;
        public final View bottomSeparator, rightSeparator;
        public final ConstraintLayout itemZoneMain;

        public ViewHolder( View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_zone);
            bottomSeparator = (View) view.findViewById(R.id.seperator_bottom);
            rightSeparator = (View) view.findViewById(R.id.seperator_right);
            imageViewAdd = (ImageView) view.findViewById(R.id.iv_add);
            itemZoneMain = (ConstraintLayout) view.findViewById(R.id.item_zone_main);

        }
    }
}
