package com.muslim.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ReadDikr extends AppCompatActivity {

    TextView titleTV, textTV;
    ArrayList<String> list = new ArrayList<>();
    int currentDikr = 0, numberDikr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_dikr);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("text");

        titleTV = (TextView) findViewById(R.id.textViewTitle);
        textTV = (TextView) findViewById(R.id.textViewText);

        titleTV.setText(title);

        new MyAsynTask(this).execute(url);

    }

    public void previous(View view) {
        currentDikr = currentDikr - 1;
        if(currentDikr == -1){
            currentDikr = numberDikr;
        }
        textTV.setText(list.get(currentDikr));
    }

    public void next(View view) {
        currentDikr = currentDikr + 1;
        if(currentDikr == (numberDikr+1)){
            currentDikr = 0;
        }
        textTV.setText(list.get(currentDikr));
    }

    public class MyAsynTask extends AsyncTask<String, String, String> {

        String data;
        private ProgressDialog dialog;

        public MyAsynTask(Context context){
            dialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(huc.getInputStream());
                data = iS2string(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String iS2string(InputStream is) throws IOException {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String result = "";
            String line;
            while((line = buffer.readLine()) != null){
                result += line;
            }
            is.close();
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("جاري التحميل !");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            getJson(data);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private void getJson(String data) {
        try {
            JSONObject o = new JSONObject(data);
            JSONArray array = o.getJSONArray(o.names().get(0).toString());
            JSONObject obj;
            for(int i=0; i<array.length(); i++){
                obj = array.getJSONObject(i);
                list.add(obj.getString("Text"));
            }
            textTV.setText(list.get(0));
            currentDikr = 0;
            numberDikr = array.length()-1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
