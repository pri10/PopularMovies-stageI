package com.example.pri.popularmovies_i;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import static com.example.pri.popularmovies_i.R.id.gridview;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();


    String API_KEY = "325098ce1b71c2bbfa060a097a4bfb86";
    private static String Base_URL = "https://api.themoviedb.org/3/movie/popular?api_key=325098ce1b71c2bbfa060a097a4bfb86";
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

    public class GridViewAdapter extends ArrayAdapter {

        private Context context;
        private int layoutResourceId;
        private ArrayList<GridItem> grid_data = new ArrayList<GridItem>();

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList<String> grid_data) {
            super(context, layoutResourceId, grid_data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
        }



        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView griditem = null;

            if (convertView == null) {
                griditem = new ImageView(context);
                griditem.setLayoutParams(new GridView.LayoutParams(200, 200));
                griditem.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                griditem = (ImageView) convertView;
            }
            griditem = grid_data.get(position);


            return griditem;
        }
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
                        Picasso.with(getApplicationContext()).load(url).into(griditem);

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
            gridView.invalidateViews();


        }
    }
}
