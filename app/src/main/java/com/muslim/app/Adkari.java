package com.muslim.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
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

public class Adkari extends AppCompatActivity {

    ListView listView;
    SearchView searchView;
    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adkari);
        listView = (ListView) findViewById(R.id.adkarList);
        searchView = (SearchView) findViewById(R.id.searchDikr);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        new MyAsynTask(this).execute(Constants.ADKAR_URL);

    }

    public void fillArrayList(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("العربية");
            JSONObject obj;
            ArrayList<Dikri> list = new ArrayList<>();
            for (int i=0; i<array.length(); i++){
                obj = array.getJSONObject(i);
                list.add(new Dikri(
                        obj.getInt("ID"),
                        obj.getString("Title"),
                        obj.getString("Audio_URL"),
                        obj.getString("Text")
                ));
            }
            fillListView(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillListView(ArrayList<Dikri> list) {
        MyAdapter myAdapter = new MyAdapter(list, this);
        listView.setAdapter(myAdapter);
    }

    public class MyAdapter extends BaseAdapter {

        ArrayList<Dikri> list = new ArrayList<Dikri>();
        Context context;

        public MyAdapter(ArrayList<Dikri> list, Context context){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.row_adkari, null);

            final TextView dikri = view.findViewById(R.id.dikri);
            Button btnRead2 = view.findViewById(R.id.btnRead2);

            dikri.setText(list.get(position).title);
            btnRead2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReadDikr.class);
                    intent.putExtra("title", list.get(position).title);
                    intent.putExtra("text", list.get(position).text);
                    startActivity(intent);
                }
            });
            return view;
        }

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
            fillArrayList(data);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

}