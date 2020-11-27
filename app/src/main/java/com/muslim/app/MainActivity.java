package com.muslim.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;
    final int READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public boolean checkPermission(Context context, String permission){
        int check = ContextCompat.checkSelfPermission(context, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }else {
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void qurani_activity(View view) {
        Intent intent = new Intent(this, Qurani.class);
        startActivity(intent);
    }

    public void adkari_activity(View view) {
        Intent intent = new Intent(this, Adkari.class);
        startActivity(intent);
    }
}
