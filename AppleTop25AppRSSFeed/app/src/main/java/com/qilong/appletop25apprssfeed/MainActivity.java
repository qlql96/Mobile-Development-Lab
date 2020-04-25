package com.qilong.appletop25apprssfeed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "INVALIDATED";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        if(savedInstanceState != null){
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }
        downloadUrl(String.format(feedUrl,feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.feeds_menu, menu);
       if(feedLimit == 10){
           menu.findItem(R.id.mnu10).setChecked(true);
       }else{
           menu.findItem(R.id.mnu25).setChecked(true);
       }
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        switch (id){
            case R.id.mnuFree:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl= "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                }
                break;
            case R.id.mnuRefresg:
                feedCachedUrl = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl,feedLimit));
        return true;

    }

    private void downloadUrl(String feedUrl){

        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            Download download = new Download();
            download.execute(feedUrl);
            feedCachedUrl= feedUrl;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);

        super.onSaveInstanceState(outState);
    }

    private class Download extends AsyncTask<String, Void, String>{
        private static final String TAG = "Download";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record,
                    parseApplications.getEntries());
            listApps.setAdapter(feedAdapter);

//
//            ArrayAdapter<AppEntry> arrayAdapter = new ArrayAdapter<AppEntry>(
//                    MainActivity.this, R.layout.list_item,parseApplications.getEntries());
//            listApps.setAdapter(arrayAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading ");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath){
            StringBuilder xmlResult = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int numChars;
                char[] inputBuffer = new char[500];
                while (true) {
                    numChars = reader.read(inputBuffer);
                    if (numChars < 0) {
                        break;
                    }
                    if (numChars> 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, numChars));
                    }
                }
                reader.close();
                return xmlResult.toString();

            }catch (MalformedURLException e){
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage() );
            }catch (IOException e){
                Log.e(TAG, "downloadXML: IO Exception" + e.getMessage() );
            }catch (SecurityException e){
                Log.e(TAG, "downloadXML: Security Exception" + e.getMessage() );
                e.printStackTrace();
            }
            return null;
        }

    }
}
