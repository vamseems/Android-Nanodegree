package com.vchamakura.popmov;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    int colWidth;
    MovieAdapter movieAdapter;
    GridView gridView;
    ArrayList<MovieItem> mMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView = (GridView) findViewById(R.id.moviesGrid);
        movieAdapter = new MovieAdapter(this, mMovies, colWidth);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Start intent to detailed view here
            }
        });
        new MovieDownloader().execute("http://api.themoviedb.org/3/discover/movie?" +
                "sort_by=popularity.desc&api_key=5d3642f7f22e6b22fd5d43e36c4560b0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            colWidth = gridView.getWidth() / 2;
            gridView.setNumColumns(2);
        }
        else{
            colWidth = gridView.getWidth()/ 3;
            gridView.setNumColumns(3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public class MovieDownloader extends AsyncTask<String, Drawable, ArrayList<MovieItem>> {

        @Override
        protected ArrayList<MovieItem> doInBackground(String... urls) {
            try {
                JSONObject moviesJSON = downloadMovies(urls[0]);
                JSONArray moviesArray = moviesJSON.getJSONArray("results");
                for (int i = 0, size = moviesArray.length(); i < size; i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);
                    String posterURL = getString(R.string.imageURLPrefix) +
                            movie.get("poster_path");
                    mMovies.add(new MovieItem(movie.get("title").toString(),
                            movie.get("id").toString(), posterURL));
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, e.toString());
            }
            return mMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {
            super.onPostExecute(movieItems);

            mMovies = movieItems;
            movieAdapter.notifyDataSetChanged();
            gridView.setAdapter(movieAdapter);
        }
    }

    private JSONObject downloadMovies(String myURL) throws IOException, JSONException {
        InputStream is = null;

        try {
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            conn.getResponseCode();
            is = conn.getInputStream();
            return readInputStream(is);
        } finally {
            if (is != null)
                is.close();
        }
    }

    public JSONObject readInputStream(InputStream is) throws IOException, JSONException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        return new JSONObject(responseStrBuilder.toString());
    }
}
