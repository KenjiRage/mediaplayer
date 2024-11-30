package com.example.mymusicapp;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar canciones con metadatos desde assets/music
        songList = loadSongsWithMetadata();

        adapter = new SongAdapter(songList, song -> {
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            intent.putExtra("songs", new Gson().toJson(songList)); // Pasar lista completa como JSON
            intent.putExtra("currentSongIndex", songList.indexOf(song)); // Índice de la canción actual
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private List<Song> loadSongsWithMetadata() {
        List<Song> songs = new ArrayList<>();
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("music");
            if (files != null) {
                for (String file : files) {
                    songs.add(extractMetadata("music/" + file));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

    private Song extractMetadata(String filePath) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String title = "Desconocido";
        String artist = "Desconocido";
        String album = "Desconocido";

        try {
            // Intentar abrir el archivo y extraer metadatos
            AssetFileDescriptor afd = getAssets().openFd(filePath);
            retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            // Extraer los metadatos
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

            // Valores predeterminados si no hay metadatos disponibles
            if (title == null) title = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
            if (artist == null) artist = "Desconocido";
            if (album == null) album = "Desconocido";

        } catch (IOException | IllegalArgumentException e) {
            // Manejar cualquier excepción (archivo no encontrado, corrupto, etc.)
            e.printStackTrace();
        } finally {
            retriever.release(); // Liberar recursos del MediaMetadataRetriever
        }

        return new Song(title, artist, album, filePath);
    }
}
