package com.vchamakura.myappportifolio;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My App Portfolio");
        setSupportActionBar(toolbar);

        final Context context = getApplicationContext();
        final Button spotifyStreamer = (Button) findViewById(R.id.spotifyStreamer);
        final Button scoresApp = (Button) findViewById(R.id.scoresApp);
        final Button libraryApp = (Button) findViewById(R.id.libraryApp);
        final Button buildItBigger = (Button) findViewById(R.id.buildItBigger);
        final Button xyzReader = (Button) findViewById(R.id.xyzReader);
        final Button capstoneApp = (Button) findViewById(R.id.capstoneApp);

        spotifyStreamer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayText((String) getText(R.string.spotify_text), context);
            }
        });

        scoresApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayText((String) getText(R.string.scores_text), context);
            }
        });

        libraryApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayText((String) getText(R.string.library_text), context);
            }
        });

        buildItBigger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayText((String) getText(R.string.bigger_text), context);
            }
        });

        xyzReader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayText((String) getText(R.string.xyz_text), context);
            }
        });

        capstoneApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayText((String) getText(R.string.capstone_text), context);
            }
        });
    }

    void DisplayText(String s, Context context){
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
