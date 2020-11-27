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
import android.os.Environment;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class List_of_Sowar_and_Media extends AppCompatActivity {

    static ArrayList<Surah> suarList = new ArrayList<>();
    static ListView listView;
    static String quari, quariUrl;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static Button btnPauseResume;
    static SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of__sowar_and__media);

        btnPauseResume = (Button) findViewById(R.id.btnPauseResume);
        listView = (ListView) findViewById(R.id.listMedia);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        Intent intent = getIntent();
        quari = intent.getStringExtra("Quari");

        allSuwar();

    }

    static String jsonResult = "";

    public void allSuwar(){
        String strUrl = "http://api.alquran.cloud/v1/surah";
        new MyAsynTask(this).execute(strUrl, "1");
    }

    public void getSuar(){
        try {
            if(!jsonResult.equals("")) {
                JSONObject obj = new JSONObject(jsonResult);
                JSONArray array = obj.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    String surah = jObj.getString("name");
                    suarList.add(new Surah(i, surah));
                }
                MyAdapter myAdapter = new MyAdapter(suarList, this);
                listView.setAdapter(myAdapter);
            }else{
                Toast.makeText(this, "تفقد الاتصال بالأنترنت", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "تفقد الاتصال بالأنترنت", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    static String number;
    static int nextS = 0;
    static int prevS = 0;

    public void listenTo(int surahNumber){
        if((surahNumber <= 114) && (surahNumber >= 1)) {
            // Get the number of surah
            number = String.valueOf(surahNumber);
            nextS = surahNumber + 1;
            prevS = surahNumber - 1;
            // Get the url of the quari from the server
            String url = Constants.ROOT_URL + "/Quari/" + quari;
            new MyAsynTask(this).execute(url, "2");
        }
    }

    static Uri myUri;

    public void playSurat(String url){
         myUri = Uri.parse(url+".mp3");
        try{
            if(mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setDataSource(this, myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Work with the seek bar
            seekBar.setMax(mediaPlayer.getDuration());
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mediaPlayer != null){
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void getUrl(){
        try {
            JSONArray array = new JSONArray(jsonResult);
            JSONObject obj = array.getJSONObject(0);
            quariUrl = obj.getString("quariurl");
            switch (number.length()){
                case 1:
                    quariUrl += "00"+number;
                    break;
                case 2:
                    quariUrl += "0"+number;
                    break;
                case 3:
                    quariUrl += number;
                    break;
            }
            playSurat(quariUrl);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void stop(View view) {
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    public void previous(View view) {
        listenTo(prevS);
    }

    public void next(View view) {
        listenTo(nextS);
    }

    boolean x = true;

    public void pause_resume(View view) {
        if(mediaPlayer != null){
            if(x == true){
                mediaPlayer.pause();
                btnPauseResume.setText(R.string.resume);
                btnPauseResume.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.icon_play), null);
                x = false;
            }else{
                mediaPlayer.start();
                btnPauseResume.setText(R.string.pause);
                btnPauseResume.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.pause_icon), null);
                x = true;
            }
        }

    }

    public void download(int surahNumber){
        number = String.valueOf(surahNumber);
        // Get the url of the quari from the server
        String url = Constants.ROOT_URL + "/Quari/" + quari;
        new MyAsynTask(this).execute(url, "3");
    }

    public void getUrl2(){
        try {
            JSONArray array = new JSONArray(jsonResult);
            JSONObject obj = array.getJSONObject(0);
            quariUrl = obj.getString("quariurl");
            switch (number.length()){
                case 1:
                    quariUrl += "00"+number;
                    break;
                case 2:
                    quariUrl += "0"+number;
                    break;
                case 3:
                    quariUrl += number;
                    break;
            }
            downloadSurah(quariUrl);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void downloadSurah(String url){
        myUri = Uri.parse(url+".mp3");
        new DownloadTask(this).execute(url+".mp3");

    }

    public class MyAdapter extends BaseAdapter {

        ArrayList<Surah> list = new ArrayList<Surah>();
        Context context;

        public MyAdapter(ArrayList<Surah> list, Context context){
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
            View view = inflater.inflate(R.layout.row_media, null);

            final TextView surah = view.findViewById(R.id.surah);
            Button btnListen = view.findViewById(R.id.btnListen);
            Button btnDownload = view.findViewById(R.id.btnDownload);

            surah.setText(list.get(position).name);
            btnListen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenTo(position+1);
                }
            });
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download(position+1);
                }
            });

            return view;
        }

    }

    public class MyAsynTask extends AsyncTask<String, String, String> {

        String data;
        String requestNo;
        private ProgressDialog dialog;

        public MyAsynTask(Context context){
            dialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                requestNo = strings[1];
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
            jsonResult = data;
            switch (requestNo){
                case "1":
                    getSuar();
                    break;
                case "2":
                    getUrl();
                    break;
                case "3":
                    getUrl2();
                    break;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    public class DownloadTask extends AsyncTask<String, Integer, String>{

        ProgressDialog dialog;
        Context context;
        PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context){
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            InputStream is = null;
            OutputStream os = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return "Server Returned HTTP" + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                String[] filen = strings[0].split("/");
                is = connection.getInputStream();
                os = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + filen[filen.length-1]);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while((count = is.read(data)) != -1){
                    if(isCancelled()){
                        is.close();
                        return null;
                    }
                    total += count;
                    if(fileLength > 0){
                        this.dialog.setProgress((int) (total * 100)/fileLength);
                    }
                    os.write(data, 0, count);
                }
            }catch(Exception e){
                return e.toString();
            }finally {
                try {
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                    if (connection != null)
                        connection.disconnect();
                }catch (IOException e){
                    return e.toString();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            this.dialog.setMessage("جاري التحميل !");
            this.dialog.setTitle("تحميل الملف");
            this.dialog.setMax(100);
            this.dialog.setIndeterminate(false);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(mWakeLock.isHeld()) {
                mWakeLock.release();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            this.dialog.setIndeterminate(false);
            this.dialog.setMax(100);
            this.dialog.setProgress(values[0]);
        }
    }
}
