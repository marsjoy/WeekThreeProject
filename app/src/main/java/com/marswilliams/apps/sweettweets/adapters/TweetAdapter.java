package com.marswilliams.apps.sweettweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.activities.TweetDetailsActivity;
import com.marswilliams.apps.sweettweets.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.marswilliams.apps.sweettweets.activities.TimelineActivity.TWEET_POSITION;

/**
 * Created by mars_williams on 9/29/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    // pass in the tweets array to the constructor

    private static List<Tweet> mTweets;
    Context context;

    // Pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Context context = holder.rootView.getContext();
        // get the data according to the position
        final Tweet tweet = mTweets.get(position);
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
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetAdapter.this.openTweetDetails(context, tweet, holder.getAdapterPosition());
            }
        });
    }

    public void openTweetDetails(Context context, Tweet tweet, int position) {
        Intent intent = new Intent(context, TweetDetailsActivity.class);
        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        intent.putExtra(TWEET_POSITION, position);
        context.startActivity(intent);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rootView)
        public View rootView;
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
        }
    }
}
