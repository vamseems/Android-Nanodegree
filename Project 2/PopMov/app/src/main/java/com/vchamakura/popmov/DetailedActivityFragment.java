package com.vchamakura.popmov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.linearlistview.LinearListView;
import com.squareup.picasso.Picasso;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailedActivityFragment extends Fragment {
    private static final String TAG = DetailedActivity.class.getName();
    private static final String imageURLPrefix = "http://image.tmdb.org/t/p/w342/";
    private static final String backDropURLPrefix = "http://image.tmdb.org/t/p/w780/";
    private static final String FAVORITES = "FAVORITES";

    View rootView;
    String movieID;

    ArrayList<JSONObject> mTrailers = new ArrayList<>();
    ArrayList<JSONObject> mReviews = new ArrayList<>();

    LinearListView trailersList;
    LinearListView reviewList;

    CheckBox favoriteButton;

    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;

    public DetailedActivityFragment() {
    }

    public static DetailedActivityFragment newInstance(MovieItem movie) {

        DetailedActivityFragment f = new DetailedActivityFragment();

        Bundle bundle = new Bundle();
        bundle.putString("movie_title", movie.title);
        bundle.putString("movie_id", movie.movieID);
        bundle.putString("movie_poster_url", movie.posterURL);
        f.setArguments(bundle);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detailed, container, false);

        Bundle bundle = this.getArguments();

        trailersList = (LinearListView) rootView.findViewById(R.id.detail_trailers);
        reviewList = (LinearListView) rootView.findViewById(R.id.detail_reviews);
        favoriteButton = (CheckBox) rootView.findViewById(R.id.detailed_favorite);

        trailerAdapter = new TrailerAdapter(getContext(), mTrailers);
        trailersList.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(getContext(), mReviews);
        reviewList.setAdapter(reviewAdapter);

        trailersList.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" +
                            mTrailers.get(position).getString("id")));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                startActivity(intent);
            }
        });

        reviewList.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse(mReviews.get(position).getString("url")));
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
                startActivity(intent);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
                Set<String> favorites = sharedPref.getStringSet(FAVORITES, new HashSet<String>());
                Set<String> newFavs = new HashSet<String>(favorites);

                if (checkBox.isChecked()) {
                    newFavs.add(movieID);
                } else {
                    newFavs.remove(movieID);
                }

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.putStringSet(FAVORITES, newFavs);
                editor.commit();
            }
        });

        if (bundle != null) {
            String mTitle = bundle.getString("movie_title");
            String mPosterURL = bundle.getString("movie_poster_url");
            movieID = bundle.getString("movie_id");

            // Set the movie poster & title passed on from the previous activity
            ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(getContext()).load(mPosterURL).into(poster);

            getActivity().setTitle(mTitle);

            if (movieID != null) {
                // Call the Async Task to fetch the movie data from the API
                new MovieDetails().execute(movieID);
            }
        }
        return rootView;
    }

    public class MovieDetails extends AsyncTask<String, Drawable, MovieDetailItem> {

        @Override
        protected MovieDetailItem doInBackground(String... movieID) {
            MovieDetailItem movie = null;
            ArrayList<JSONObject> videos = new ArrayList<>();
            ArrayList<JSONObject> reviews = new ArrayList<>();
            try {
                // Fetch & parse movie JSON data.
                JSONObject movieJSON = downloadData("https://api.themoviedb.org/3/movie/"
                        + movieID[0] + "?api_key=" + BuildConfig.TMDB_API_KEY);
                // Fetch & parse videos JSON data.
                JSONObject videosJSON = downloadData("https://api.themoviedb.org/3/movie/"
                        + movieID[0] + "/videos?api_key=" + BuildConfig.TMDB_API_KEY);
                // Fetch & parse reviews JSON data.
                JSONObject reviewsJSON = downloadData("https://api.themoviedb.org/3/movie/"
                        + movieID[0] + "/reviews?api_key=" + BuildConfig.TMDB_API_KEY);

                // Parse video data
                JSONArray videoResults = videosJSON.getJSONArray("results");
                for (int i = 0 ; i < videoResults.length() ; i++) {
                    JSONObject video = new JSONObject();
                    video.put("id", ((JSONObject)videoResults.get(i)).getString("key"));
                    video.put("site", ((JSONObject)videoResults.get(i)).getString("site"));
                    video.put("type", ((JSONObject)videoResults.get(i)).getString("type"));
                    videos.add(video);
                }

                // Parse review data
                JSONArray reviewResults = reviewsJSON.getJSONArray("results");
                for (int i = 0 ; i < reviewResults.length() ; i++) {
                    JSONObject review = new JSONObject();
                    review.put("author", ((JSONObject)reviewResults.get(i)).getString("author"));
                    review.put("content", ((JSONObject)reviewResults.get(i)).getString("content"));
                    review.put("url", ((JSONObject)reviewResults.get(i)).getString("url"));
                    reviews.add(review);
                }

                movie = new MovieDetailItem(movieJSON.getString("original_title"),
                        movieJSON.getString("id"), imageURLPrefix +
                        movieJSON.getString("poster_path"), backDropURLPrefix +
                        movieJSON.getString("backdrop_path"), movieJSON.getString("overview"),
                        movieJSON.getString("vote_average"), movieJSON.getString("release_date"),
                        videos, reviews);

            } catch (IOException | JSONException e) {
                Log.e(TAG, e.toString());
            }
            return movie;
        }

        @Override
        protected void onPostExecute(MovieDetailItem movie) {
            super.onPostExecute(movie);

            mTrailers = movie.videos;
            mReviews = movie.reviews;
            if (getContext() != null) {
                trailerAdapter = new TrailerAdapter(getContext(), mTrailers);
                trailerAdapter.notifyDataSetChanged();
                trailersList.setAdapter(trailerAdapter);

                reviewAdapter = new ReviewAdapter(getContext(), mReviews);
                reviewAdapter.notifyDataSetChanged();
                reviewList.setAdapter(reviewAdapter);

                SharedPreferences sharedPref = getActivity().getSharedPreferences("MyPrefs",
                        Context.MODE_PRIVATE);
                Set<String> favorites = sharedPref.getStringSet(FAVORITES, new HashSet<String>());

                if (favorites.contains(movieID)) {
                    favoriteButton.setChecked(true);
                }

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
    }

    private JSONObject downloadData (String myURL) throws IOException, JSONException {
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
