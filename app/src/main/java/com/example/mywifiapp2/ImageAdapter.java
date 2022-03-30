package com.example.mywifiapp2;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywifiapp2.R;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

// Adapters, viewholders for the StorageChoser recyclerview

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;
    ArrayList<String> imagelist;
    OnNoteListener monNoteListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView currentimages;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);
            currentimages = itemView.findViewById(R.id.currentimage);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public ImageAdapter(Context context, ArrayList<String> imagelist, OnNoteListener onNoteListener) {
        this.context = context;
        this.imagelist = imagelist;
        this.monNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_image,parent,false);
        return new ViewHolder(view, monNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Picasso.get().load(imagelist.get(position)).into(holder.currentimages);
    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
