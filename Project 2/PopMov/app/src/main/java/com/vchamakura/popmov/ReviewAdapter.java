package com.vchamakura.popmov;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vchamakura on 17/04/16 at 12:01 AM.
 * Copyrights reserved to Vamsee Chamakura
 */
public class ReviewAdapter extends BaseAdapter {
    private Context mContext;
    private final LayoutInflater mInflater;
    private ArrayList<JSONObject> mReviews;

    public ReviewAdapter(Context context, ArrayList<JSONObject> items) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mReviews = items;
    }

    public int getCount() {
        return mReviews.size();
    }

    public JSONObject getItem(int position) {
        if (mReviews.size() > 0)
            return mReviews.get(position);
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        JSONObject review = mReviews.get(position);
        if (view == null) {
            view = mInflater.inflate(R.layout.review_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) view.getTag();

        try {
            viewHolder.nameView.setText(review.getString("author"));
            viewHolder.content.setText(Html.fromHtml(review.getString("content")));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    public static class ViewHolder {
        public final TextView content;
        public final TextView nameView;

        public ViewHolder(View view) {
            content = (TextView) view.findViewById(R.id.review_content);
            nameView = (TextView) view.findViewById(R.id.review_user);
        }
    }
}
