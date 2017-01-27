package com.example.pri.popularmovies_i;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

import static com.example.pri.popularmovies_i.R.id.gridview;

public class MainActivity extends AppCompatActivity {
String API_KEY="325098ce1b71c2bbfa060a097a4bfb86";
    String url = " http://image.tmdb.org/t/p/" + "w185" + "/WLQN5aiQG8wc9SeKwixW7pAR8K.jpg"+API_KEY;
    GridView gridView;
    ImageView griditem;
Boolean downloading;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<GridItem> grid_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(gridview);
        griditem = (ImageView) findViewById(R.id.grid_item);
        grid_data = new ArrayList<>();
        gridViewAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, grid_data);
        gridView.setAdapter(gridViewAdapter);

        DownloadImages downloadImages = new DownloadImages();
        downloadImages.execute(url);

    }

    public class GridViewAdapter extends ArrayAdapter {

        private Context context;
        private int layoutResourceId;
        private ArrayList<GridItem> grid_data = new ArrayList<GridItem>();

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList<GridItem> grid_data) {
            super(context, layoutResourceId, grid_data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.grid_data = grid_data;
        }

        public void setGridData(ArrayList<GridItem> mGridData) {
            this.grid_data = mGridData;
            notifyDataSetChanged();
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

            Picasso.with(getApplicationContext()).load(url).into(griditem);
            return griditem;
        }
    }

    public class DownloadImages extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloading = true;
        }

        protected Void doInBackground(String... params) {
            String urlString = params[0];

            try {
                URL theUrl = new URL(urlString);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(theUrl.openConnection().getInputStream(),
                                "UTF-8"));
                String json = reader.readLine();
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jArray = jsonObject.getJSONArray("data");

                url = jsonObject.getJSONObject("pagination").getString("next_url");


                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    JSONObject imageObject = oneObject.getJSONObject("images");
                    JSONObject thumbnailObject = imageObject.getJSONObject("thumbnail");
                    String urlBitmap = thumbnailObject.getString("url");

                    URL downloadURL = new URL(urlBitmap);
                    HttpURLConnection conn = (HttpURLConnection) downloadURL.openConnection();
                    InputStream inputStream = conn.getInputStream();

                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            downloading = false;
            gridViewAdapter.notifyDataSetChanged();
            gridView.invalidateViews();
        }
    }
}