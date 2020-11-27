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
        useQuari("El-Affasi");
    }

    public void ajmi(View view) {
        useQuari("El-Ajmi");
    }

    public void basit_warsh(View view) {
        useQuari("Basit-Warsh");
    }

    public void basit_hafs(View view) {
        useQuari("Basit-Hafs");
    }

    public void yacine(View view) {
        useQuari("Yacine");
    }

    public void mailki(View view) {
        useQuari("El-Mailki");
    }

    public void dosri(View view) {
        useQuari("El-Dosri");
    }

    public void useQuari(String name){
        Intent intent = new Intent(this, List_of_Sowar_and_Media.class);
        intent.putExtra("Quari", name);
        startActivity(intent);
    }
}
