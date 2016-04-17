package com.vchamakura.popmov;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vchamakura on 16/04/16 at 9:53 PM.
 * Copyrights reserved to Vamsee Chamakura
 */
public class TrailerAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<JSONObject> mTrailers;

    public TrailerAdapter(Context context, ArrayList<JSONObject> items) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTrailers = items;
    }

    public int getCount() {
        return mTrailers.size();
    }

    public JSONObject getItem(int position) {
        if (mTrailers.size() > 0)
            return mTrailers.get(position);
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        JSONObject trailer = mTrailers.get(position);

        if (view == null) {
            view = mInflater.inflate(R.layout.trailer_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) view.getTag();

        String youtubeThumbURL = null;
        try {
            youtubeThumbURL = "http://img.youtube.com/vi/" + trailer.getString("id") + "/0.jpg";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Load Movie Poster into Image View
        viewHolder.imageView.setAdjustViewBounds(true);
        Picasso.with(mContext).load(youtubeThumbURL).into(viewHolder.imageView);

        try {
            viewHolder.nameView.setText(trailer.getString("site") + ": " + trailer.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.trailer_image_view);
            nameView = (TextView) view.findViewById(R.id.trailer_text_view);
        }
    }
}
