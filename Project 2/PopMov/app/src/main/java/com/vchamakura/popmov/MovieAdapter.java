package com.vchamakura.popmov;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vchamakura on 01/03/16 at 5:43 PM.
 * Copyrights reserved to Vamsee Chamakura
 */

public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MovieItem> mMovies;

    public MovieAdapter(Context context, ArrayList<MovieItem> items) {
        mContext = context;

        this.mMovies = items;
    }

    public int getCount() {
        return mMovies.size();
    }

    public MovieItem getItem(int position) {
        if (mMovies.size() > 0)
            return mMovies.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (mMovies.size() > 0)
            return Long.parseLong(mMovies.get(position).movieID);
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load Movie Poster into Image View
        Picasso.with(mContext).load(mMovies.get(position).posterURL).into(imageView);

        return imageView;
    }
}
