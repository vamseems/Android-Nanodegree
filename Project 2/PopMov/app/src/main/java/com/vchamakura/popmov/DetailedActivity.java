package com.vchamakura.popmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailedActivity extends AppCompatActivity {
    private static final String DTAG = "DFRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            // Get Extra strings from the intent
            String mTitle = intent.getStringExtra("movie_title");
            String mPosterURL = intent.getStringExtra("movie_poster_url");
            String movieID = intent.getStringExtra("movie_id");

            MovieItem movie = new MovieItem(mTitle, movieID, mPosterURL);

            DetailedActivityFragment details = DetailedActivityFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailed_fragment_container, details, DTAG)
                    .commit();
        }
    }

}
