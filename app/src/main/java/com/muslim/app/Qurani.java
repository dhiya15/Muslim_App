package com.muslim.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Qurani extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qurani);
    }

    public void affasi(View view) {
        useQuari(
                "El-Affasi",
                "http://server8.mp3quran.net/afs/"
        );
    }

    public void ajmi(View view) {
        useQuari(
                "El-Ajmi",
                "http://server10.mp3quran.net/ajm/128/"
        );
    }

    public void basit_warsh(View view) {
        useQuari(
                "Basit-Warsh",
                "http://server10.mp3quran.net/basit_warsh/"
        );
    }

    public void basit_hafs(View view) {
        useQuari(
                "Basit-Hafs",
                "http://server7.mp3quran.net/basit/"
        );
    }

    public void yacine(View view) {
        useQuari(
                "Yacine",
                "http://server11.mp3quran.net/qari/"
                );
    }

    public void mailki(View view) {
        useQuari(
                "El-Mailki",
                "http://server12.mp3quran.net/maher/"
        );
    }

    public void dosri(View view) {
        useQuari(
                "El-Dosri",
                "http://server10.mp3quran.net/ibrahim_dosri_warsh/"
        );
    }

    public void useQuari(String name, String url){
        Intent intent = new Intent(this, List_of_Sowar_and_Media.class);
        intent.putExtra("Quari", name);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}
