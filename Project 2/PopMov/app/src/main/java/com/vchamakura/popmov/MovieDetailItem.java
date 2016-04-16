package com.vchamakura.popmov;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vchamakura on 04/03/16 at 1:07 AM.
 * Copyrights reserved to Vamsee Chamakura
 */
public class MovieDetailItem {
    final String title;
    final String movieID;
    final String posterURL;
    final String backDrop;
    final String plot;
    final String voteAverage;
    final String releaseDate;
    final ArrayList<JSONObject> videos;

    public MovieDetailItem(String title, String movieID, String posterURL, String backDrop, String plot,
                     String voteAverage, String releaseDate, ArrayList<JSONObject> videos) {
        this.title = title;
        this.movieID = movieID;
        this.posterURL = posterURL;
        this.backDrop = backDrop;
        this.plot = plot;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.videos = videos;
    }

}
