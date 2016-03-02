package com.vchamakura.popmov;

/**
 * Created by vchamakura on 01/03/16 at 7:05 PM.
 * Copyrights reserved to Vamsee Chamakura
 */
public class MovieItem {
    final String title;
    final String movieID;
    final String posterURL;

    public MovieItem(String title, String movieID, String posterURL) {
        this.title = title;
        this.movieID = movieID;
        this.posterURL = posterURL;
    }
}
