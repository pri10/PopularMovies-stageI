package com.example.pri.popularmovies_i;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.data;
import static com.example.pri.popularmovies_i.R.id.grid_item;
import static com.example.pri.popularmovies_i.R.id.gridview;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();


//    String API_KEY = getString(R.string.api);
    private  String Base_URL = "https://api.themoviedb.org/3/movie/popular?api_key=325098ce1b71c2bbfa060a097a4bfb86";
    //  String url = " http://image.tmdb.org/t/p/"+"w185/"+"https://api.themoviedb.org/3/movie/popular?api_key="+API_KEY;
  //  String poster_path = "5gJkVIVU7FDp7AfRAbPSvvdbre2.jpg";
    GridView gridView;
    ImageView griditem;
    Boolean downloading;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<String> grid_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(gridview);
        griditem = (ImageView) findViewById(R.id.grid_item);
        grid_data = new ArrayList<>();
        DownloadImages downloadImages = new DownloadImages();
        downloadImages.execute();

    }


    public class DownloadImages extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloading = true;
        }

        protected Void doInBackground(String... params) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Base_URL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray image = jsonObj.getJSONArray("results");

                    // looping through All Contacts
                    for (int i = 0; i < image.length(); i++) {
                        JSONObject c = image.getJSONObject(i);

                        String original_title = c.getString("original_title");
                        String poster_path = c.getString("poster_path");
                        String overview= c.getString("overview");
                        String vote_average = c.getString("vote_average");
                        String release_date = c.getString("release_date");

                        // tmp hash map for single contact
                        HashMap<String, String> images = new HashMap<>();

                        // adding each child node to HashMap key => value
                        images.put("original_title", original_title);
                        images.put("poster_path", poster_path);
                        images.put("overview", overview);
                        images.put("vote_average", vote_average);
                        images.put("release_date", release_date);

                        // adding contact to contact list
                        String url="http://image.tmdb.org/t/p/w185"+poster_path;
                        grid_data.add(url);


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }


        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            downloading = false;

           gridView.setAdapter(new GridViewAdapter(getApplicationContext(),grid_data));

        }
    }

    public class GridViewAdapter extends BaseAdapter {

        private Context context;
        private int layoutResourceId;
        private ArrayList<String> data;

        public GridViewAdapter(Context context, ArrayList<String> grid_data) {
            this.context = context;
            this.data=grid_data;
        }


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView=new ImageView(context);
           imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setLayoutParams(new GridView.LayoutParams(500,500));
            Picasso.with(MainActivity.this).load(data.get(position)).into(imageView);
            return imageView;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.popular) {
                update();
            return true;
        }

        if (id == R.id.top_rated) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void update(){
        String top="http://api.themoviedb.org/3/discover/movie?sort_by=toprated.desc&api_key=325098ce1b71c2bbfa060a097a4bfb86";
        DownloadImages downloadImages = new DownloadImages();
        downloadImages.execute(top);
Picasso.with(MainActivity.this).load(top).into(griditem);
    }
}
