package com.marswilliams.apps.sweettweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mars_williams on 9/29/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    // pass in the tweets array to the constructor

    private static List<Tweet> mTweets;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    public static int getCount() {
        return mTweets.size();
    }

    public static Tweet getAt(int position) {
        return mTweets.get(position);
    }

    // create Viewholder class
    // for each row, inflate the layout and cache references into viewholder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.tweet_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to the position
        Tweet tweet = mTweets.get(position);
        Glide.with(holder.ivProfileImage.getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_profile_image_placeholder)
                .bitmapTransform(new RoundedCornersTransformation(holder.ivProfileImage.getContext(), 25, 0))
                .into(holder.ivProfileImage);

        // populate the views according to this data
        holder.tvUserName.setText(tweet.getUser().getName());
        holder.tvUserScreenName.setText(holder.tvUserScreenName.getContext()
                .getString(R.string.formatted_user_screen_name, tweet.getUser().getScreenName()));
        holder.tvBody.setText(tweet.getBody());
        holder.tvCreatedAt.setText(tweet.getRelativeCreatedAt());
    }

    @Override
    public int getItemCount() {
        return this.mTweets.size();
    }

    public void clear() {
        int size = this.mTweets.size();
        this.mTweets.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvUserScreenName;
        public TextView tvBody;
        public TextView tvCreatedAt;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvUserScreenName = (TextView) itemView.findViewById(R.id.tvUserScreenName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
        }
    }
}
