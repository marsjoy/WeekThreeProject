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
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mars_williams on 9/29/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    // pass in the tweets array to the constructor

    private static List<Tweet> mTweets;
    public TwitterClient client;
    Context context;
    private TweetAdapterListener mListener;

    // Desfine an interface required by the ViewHolder
    public interface TweetAdapterListener {
        void onItemSelected(View view, int position);
    }

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    public static int getCount() {
        return mTweets.size();
    }

    public static Tweet getAt(int position) {
        return mTweets.get(position);
    }

    // create ViewHolder class
    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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
        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_profile_image_placeholder)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvUserScreenName)
        TextView tvUserScreenName;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvCreatedAt)
        TextView tvCreatedAt;
        @BindView(R.id.ibRetweet)
        ImageView ibRetweet;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            client = TwitterApplication.getRestClient();
        }

        @Override
        public void onClick(View v) {
            if(mListener != null) {
                // Get the position of the row element
                int position = getAdapterPosition();
                // Fire the listener callback
                mListener.onItemSelected(v, position);
            }
        }
    }
}
