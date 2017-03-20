package com.example.pri.popularmovies_i;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.pri.popularmovies_i.R.id.gridview;

public class Details_Activity extends AppCompatActivity {
String original_title;
    String poster_path;
    TextView postertitle;
    ImageView posterimage;
    private String TAG = Details_Activity.class.getSimpleName();
    Boolean downloading;
    private  String Base_URL = "https://api.themoviedb.org/3/movie/popular?api_key=325098ce1b71c2bbfa060a097a4bfb86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_);
        postertitle= (TextView) findViewById(R.id.title1);
        posterimage= (ImageView) findViewById(R.id.thumbnail);
        //gridView = (GridView) findViewById(gridview);
        //griditem = (ImageView) findViewById(R.id.grid_item);
        //grid_data = new ArrayList<>();
        Details_Activity.DownloadImages downloadImages = new Details_Activity.DownloadImages();
        downloadImages.execute();
    }

    public class DownloadImages extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloading = true;
        }

        public Void doInBackground(String... params) {
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

                        original_title = c.getString("original_title");
                        poster_path = c.getString("poster_path");
                        String overview= c.getString("overview");
                        String vote_average = c.getString("vote_average");
                        String release_date = c.getString("release_date");


                        HashMap<String, String> images = new HashMap<>();


                        images.put("original_title", original_title);
                        images.put("poster_path", poster_path);
                        images.put("overview", overview);
                        images.put("vote_average", vote_average);
                        images.put("release_date", release_date);


                        //String url="http://image.tmdb.org/t/p/w185"+poster_path;
                        //grid_data.add(url);


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
            postertitle.setText(original_title);

            Picasso.with(Details_Activity.this).load(Base_URL).into(posterimage);

            //gridView.setAdapter(new MainActivity.GridViewAdapter(getApplicationContext(),grid_data));

        }
    }
}
