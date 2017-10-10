package com.marswilliams.apps.sweettweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.activities.TweetDetailsActivity;
import com.marswilliams.apps.sweettweets.activities.TweetReplyActivity;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.marswilliams.apps.sweettweets.activities.TimelineActivity.TWEET_POSITION;

/**
 * Created by mars_williams on 9/29/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    // pass in the tweets array to the constructor

    private static List<Tweet> mTweets;
    Context context;
    static ViewHolder viewHolder;
    TwitterClient client;

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
    
    public static Tweet getTweet() { return getAt(viewHolder.getAdapterPosition()); }

    // create ViewHolder class
    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.tweet_item, parent, false);
        viewHolder = new ViewHolder(tweetView);

        client = TwitterApplication.getRestClient();

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
        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetAdapter.this.openTweetReply(context, tweet, holder.getAdapterPosition());
            }
        });
        holder.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tweet tweet = getTweet();
                if (tweet.isRetweeted()) {
                    unretweetTweet(tweet);
                } else {
                    retweetTweet(tweet);
                }
            }
        });
        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tweet tweet = getTweet();
                if (tweet.isFavorited()) {
                    unfavoriteTweet(tweet);
                } else {
                    favoriteTweet(tweet);
                }
            }
        });
    }

    public void openTweetDetails(Context context, Tweet tweet, int position) {
        Intent intent = new Intent(context, TweetDetailsActivity.class);
        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        intent.putExtra(TWEET_POSITION, position);
        context.startActivity(intent);
    }

    public void openTweetReply(Context context, Tweet tweet, int position) {
        Intent intent = new Intent(context, TweetReplyActivity.class);
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

    public void retweetTweet(final Tweet tweet) {
        client.retweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.setRetweeted(true);
                    if (tweet.getRetweetCount() < newTweet.getRetweetCount()) {
                        tweet.setRetweetCount(newTweet.getRetweetCount());
                    }
                    setRetweeted(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unretweetTweet(final Tweet tweet) {
        client.unRetweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.setRetweeted(false);
                    if (tweet.getRetweetCount() > newTweet.getRetweetCount()) {
                        tweet.setRetweetCount(newTweet.getRetweetCount());
                    }
                    setRetweeted(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.unable_to_retweet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void favoriteTweet(final Tweet tweet) {
        client.favoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    Tweet newTweet = Tweet.fromJSON(response);
                    if (!newTweet.isFavorited()) {
                        tweet.setFavorited(true);
                    }
                    if (newTweet.getFavoriteCount() < oldFavoriteCount + 1) {
                        tweet.setFavoriteCount(oldFavoriteCount + 1);
                    }
                    setFavorited(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unfavoriteTweet(final Tweet tweet) {
        client.unfavoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    Tweet newTweet = Tweet.fromJSON(response);
                    if (newTweet.isFavorited()) {
                        tweet.setFavorited(false);
                    }
                    if (newTweet.getFavoriteCount() > oldFavoriteCount - 1) {
                        tweet.setFavoriteCount(oldFavoriteCount - 1);
                    }
                    setFavorited(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_favorite_tweet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setFavorited(final Tweet tweet) {
        // Set the tweet favorited status
        if (tweet.isFavorited()) {
            viewHolder.ibFavorite.setImageResource(R.drawable.ic_unfavorite);
        } else {
            viewHolder.ibFavorite.setImageResource(R.drawable.ic_favorite);
        }
        // Set the number of favorites
        if (tweet.getFavoriteCount() > 0) {
            viewHolder.tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            viewHolder.tvFavoriteCount.setText(String.valueOf(0));
        }
    }

    public void setRetweeted(final Tweet tweet) {
        // Set the retweeted status
        if (tweet.isRetweeted()) {
            viewHolder.ibRetweet.setImageResource(R.drawable.ic_unretweet);
        } else {
            viewHolder.ibRetweet.setImageResource(R.drawable.ic_retweet);
        }
        // Set the number of retweets
        if (tweet.getRetweetCount() > 0) {
            viewHolder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            viewHolder.tvRetweetCount.setText(String.valueOf(0));
        }
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
        ImageButton ibRetweet;
        @BindView(R.id.tvRetweetCount)
        TextView tvRetweetCount;

        @BindView(R.id.ibFavorite)
        ImageButton ibFavorite;
        @BindView(R.id.tvFavoriteCount)
        TextView tvFavoriteCount;

        @BindView(R.id.ibReply)
        ImageButton ibReply;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
