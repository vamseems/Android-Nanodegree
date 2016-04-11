package com.vchamakura.popmov;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DetailedActivityFragment extends Fragment {
    private static final String TAG = DetailedActivity.class.getName();

    View rootView;

    public DetailedActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detailed, container, false);
        // Get the intent
        Intent intent = getActivity().getIntent();

        // Get Extra strings from the intent
        String mTitle = intent.getStringExtra("movie_title");
        String mPosterURL = intent.getStringExtra("movie_poster_url");
        String movieID = intent.getStringExtra("movie_id");

        // Set the movie poster & title passed on from the previous activity
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
        Picasso.with(getContext()).load(mPosterURL).into(poster);

        getActivity().setTitle(mTitle);


        // Call the Async Task to fetch the movie data from the API
        new MovieDetails().execute("https://api.themoviedb.org/3/movie/" + movieID + "?api_key=" +
                BuildConfig.TMDB_API_KEY);
        return rootView;
    }

    public class MovieDetails extends AsyncTask<String, Drawable, MovieDetailItem> {

        @Override
        protected MovieDetailItem doInBackground(String... urls) {
            MovieDetailItem movie = null;
            try {
                // Fetch & parse movie JSON data.
                JSONObject movieJSON = downloadMovies(urls[0]);
                movie = new MovieDetailItem(movieJSON.getString("original_title"),
                        movieJSON.getString("id"), getString(R.string.imageURLPrefix) +
                        movieJSON.getString("poster_path"), getString(R.string.backDropURLPrefix) +
                        movieJSON.getString("backdrop_path"), movieJSON.getString("overview"),
                        movieJSON.getString("vote_average"), movieJSON.getString("release_date"));

            } catch (IOException | JSONException e) {
                Log.e(TAG, e.toString());
            }
            return movie;
        }

        @Override
        protected void onPostExecute(MovieDetailItem movie) {
            super.onPostExecute(movie);

            final List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May",
                    "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

            // Parse the release date to desired(Month 'YYYY) format
            int year = Integer.parseInt(movie.releaseDate.split("-")[0]);
            int month = Integer.parseInt(movie.releaseDate.split("-")[1]);
            String releaseDate = months.get(month - 1) + " " + year;

            // Get all the required elements in the view
            ImageView backDrop = (ImageView) rootView.findViewById(R.id.backDrop);
            TextView movieTitle = (TextView) rootView.findViewById(R.id.detailed_movie_title);
            TextView movieDate = (TextView) rootView.findViewById(R.id.detailed_movie_release_date);
            TextView rating = (TextView) rootView.findViewById(R.id.detailed_movie_ratings_number);
            RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.detailed_movie_ratings);
            TextView moviePlot = (TextView) rootView.findViewById(R.id.detailed_movie_plot);

            // Change color of the stars in the RatingsBar
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

            // Set the elements in the view to the movie details
            Picasso.with(getContext()).load(movie.backDrop).into(backDrop);
            movieTitle.setText(movie.title);
            movieDate.setText(releaseDate);
            rating.setText(movie.voteAverage);
            ratingBar.setRating(Float.valueOf(movie.voteAverage));
            moviePlot.setText(movie.plot);
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
