package com.vchamakura.popmov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    private static final String DTAG = "DFRAG";

    private boolean mTwoPane;

    public boolean ismTwoPane() {
        return mTwoPane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.detailed_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detailed_fragment_container,
                                new DetailedActivityFragment(), DTAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(MovieItem movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            DetailedActivityFragment details = DetailedActivityFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailed_fragment_container,
                            details, DTAG)
                    .commit();
        } else {
            // Make a new intent to go to Detailed Activity
            Intent detailedMovieIntent = new Intent(this, DetailedActivity.class);

            // Put Extra strings into the into to pass over to Detailed Activity
            detailedMovieIntent.putExtra("movie_title", movie.title);
            detailedMovieIntent.putExtra("movie_id", movie.movieID);
            detailedMovieIntent.putExtra("movie_poster_url", movie.posterURL);

            // Start the Detailed Activity
            startActivity(detailedMovieIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

}
