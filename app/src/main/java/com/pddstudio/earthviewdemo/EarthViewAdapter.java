package com.pddstudio.earthviewdemo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pddstudio.earthview.EarthWallpaper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class EarthViewAdapter extends RecyclerView.Adapter<EarthViewAdapter.ViewHolder> {

    private List<EarthWallpaper> itemData;
    private final Context context;
    OnItemClickListener onItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public EarthViewAdapter(@Nullable Collection<EarthWallpaper> data, Context context) {
        if(data == null) {
            this.itemData = new ArrayList<>();
        } else {
            Log.d("EarthViewAdapter", "Adapter initialized with data count: " + data.size());
            this.itemData = new ArrayList<>(data);
        }
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.earth_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, onItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EarthWallpaper earthWallpaper = itemData.get(position);
        holder.textView.setText(earthWallpaper.getFormattedWallpaperTitle());

        Picasso.with(context).load(earthWallpaper.getWallThumbUrl()).into(holder.imageView);
    }

    public void addItem(EarthWallpaper wallpaper) {
        if(itemData != null) this.itemData.add(wallpaper);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean hasOnItemClickListener() {
        return this.onItemClickListener != null;
    }

    public EarthWallpaper getItemAtPosition(int position) {
        if(position < 0 || position >= itemData.size()) {
            return null;
        } else {
            return itemData.get(position);
        }
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textView;
        public OnItemClickListener onItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.wall);
            textView = (TextView) itemView.findViewById(R.id.name);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}