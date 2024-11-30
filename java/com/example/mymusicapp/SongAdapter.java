package com.example.mymusicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<Song> songs;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongAdapter(List<Song> songs, OnItemClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song, listener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView artistTextView;
        private final TextView albumTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            artistTextView = itemView.findViewById(R.id.artistTextView);
            albumTextView = itemView.findViewById(R.id.albumTextView);
        }

        public void bind(Song song, OnItemClickListener listener) {
            titleTextView.setText(song.getTitle());
            artistTextView.setText(song.getArtist());
            albumTextView.setText(song.getAlbum());
            itemView.setOnClickListener(v -> listener.onItemClick(song));
        }
    }
}
