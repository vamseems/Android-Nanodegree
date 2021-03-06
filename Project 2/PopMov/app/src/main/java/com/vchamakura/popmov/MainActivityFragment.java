package com.vchamakura.popmov;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.HashSet;
import java.util.Set;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivity.class.getName();
    private static final String SORT_ORDER = "SORT_ORDER";
    private static final String FAVORITES = "FAVORITES";

    String sortOrder = "popularity";
    MovieAdapter movieAdapter;
    GridView gridView;
    ArrayList<MovieItem> mMovies = new ArrayList<>();
    Set<String> favorites;

    public MainActivityFragment() {
    }

    public interface Callback {
        void onItemSelected(MovieItem movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get the Grid View & set it's adapter to MovieAdapter
        gridView = (GridView) rootView.findViewById(R.id.moviesGrid);
        movieAdapter = new MovieAdapter(getContext(), mMovies);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // Write sort order to shared preferences file
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SORT_ORDER, sortOrder);
                editor.apply();

                ((Callback) getActivity()).onItemSelected(mMovies.get(position));
            }
        });

        // Read sort order from shared preferences file
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sortOrder = sharedPref.getString(SORT_ORDER, "popularity");

        // Call the Async Task to fetch movies from the API
        new MovieDownloader().execute("http://api.themoviedb.org/3/discover/movie?" +
                "sort_by=" + sortOrder + ".desc&api_key=" + BuildConfig.TMDB_API_KEY);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Check orientation and set number of columns in the Grid View
        int orientation = this.getResources().getConfiguration().orientation;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float sw = Math.min(dpHeight, dpWidth);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs",
                Context.MODE_PRIVATE);
        favorites = sharedPref.getStringSet(FAVORITES, new HashSet<String>());

        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (sw >= 600) {
                gridView.setNumColumns(1);
            } else {
                gridView.setNumColumns(2);
            }
        }
        else {
            if (sw >= 600) {
                gridView.setNumColumns(2);
            } else {
                gridView.setNumColumns(3);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        if (sortOrder.contentEquals("popularity")) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_popularity.setChecked(true);
            }
        } else if (sortOrder.contentEquals("vote_average")) {
            if (!action_sort_by_rating.isChecked()) {
                action_sort_by_rating.setChecked(true);
            }
        } else if (sortOrder.contentEquals("favorite")) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_favorite.setChecked(true);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        if (id == R.id.action_sort_by_popularity) {
            sortOrder = "popularity";

            // Call the Async Task to fetch movies from the API based on popularity
            new MovieDownloader().execute("http://api.themoviedb.org/3/discover/movie?" +
                    "sort_by=popularity.desc&api_key=" + BuildConfig.TMDB_API_KEY);
        } else if (id == R.id.action_sort_by_rating) {
            sortOrder = "vote_average";

            // Call the Async Task to fetch movies from the API based on highest-rated
            new MovieDownloader().execute("http://api.themoviedb.org/3/discover/movie?" +
                    "sort_by=vote_average.desc&api_key=" + BuildConfig.TMDB_API_KEY);
        } else if (id == R.id.action_sort_by_favorite) {
            sortOrder = "favorite";

            // Call the Async Task to fetch movies from the API based on favorites
            new MovieDownloader().execute("");
        } else if (id == R.id.action_refresh) {
            new MovieDownloader().execute("http://api.themoviedb.org/3/discover/movie?" +
                    "sort_by=" + sortOrder + ".desc&api_key=" + BuildConfig.TMDB_API_KEY);
        }

        return super.onOptionsItemSelected(item);
    }

    public class MovieDownloader extends AsyncTask<String, Drawable, ArrayList<MovieItem>> {

        @Override
        protected ArrayList<MovieItem> doInBackground(String... urls) {
            if (sortOrder == "favorite") {
                mMovies.clear();
                SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
                favorites = sharedPref.getStringSet(FAVORITES, new HashSet<String>());
                for (String movieID : favorites) {
                    try {
                        JSONObject movie = downloadMovies("http://api.themoviedb.org/3/movie/" +
                                movieID + "?api_key=5d3642f7f22e6b22fd5d43e36c4560b0");
                        String posterURL = getString(R.string.imageURLPrefix) +
                                movie.get("poster_path");
                        mMovies.add(new MovieItem(movie.get("title").toString(),
                                movie.get("id").toString(), posterURL));
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            } else {
                try {
                    // Fetch & parse movies JSONArray data.
                    JSONObject moviesJSON = downloadMovies(urls[0]);
                    JSONArray moviesArray = moviesJSON.getJSONArray("results");
                    mMovies.clear();
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
            }
            return mMovies;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> movieItems) {
            super.onPostExecute(movieItems);

            // Update the movies and notify data changed to Grid View
            mMovies = movieItems;
            movieAdapter.notifyDataSetChanged();
            gridView.setAdapter(movieAdapter);
        }
    }

    private JSONObject downloadMovies(String myURL) throws IOException, JSONException {
        InputStream is = null;

        try {
            // Make URL object from the given string and perform the GET request
            URL url = new URL(myURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Start the connection
            conn.connect();
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
            }
            return readInputStream(is);
        } finally {
            if (is != null)
                // Close the connection
                is.close();
        }
    }

    public JSONObject readInputStream(InputStream is) throws IOException, JSONException {
        // Read input stream and return a JSON object of the obtained data
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        return new JSONObject(responseStrBuilder.toString());
    }
}
