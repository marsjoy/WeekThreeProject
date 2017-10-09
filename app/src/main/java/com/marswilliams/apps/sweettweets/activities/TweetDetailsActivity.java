package com.marswilliams.apps.sweettweets.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.marswilliams.apps.sweettweets.activities.TimelineActivity.REQUEST_CODE_DETAILS;
import static com.marswilliams.apps.sweettweets.activities.TimelineActivity.TWEET_POSITION;


/**
 * Created by mars_williams on 10/6/17.
 */

public class TweetDetailsActivity extends AppCompatActivity {
    @BindView(R.id.ivProfileImageDetails)
    ImageView ivProfileImage;
    @BindView(R.id.tvUserNameDetails)
    TextView tvUserName;
    @BindView(R.id.tvScreenNameDetails)
    TextView tvScreenName;
    @BindView(R.id.tvCreatedAtDetails)
    TextView tvCreatedAt;
    @BindView(R.id.tvBodyDetails)
    TextView tvBody;
    @BindView(R.id.ivMediaImageDetails)
    ImageView ivMediaImageDetails;
    @BindView(R.id.ivMediaVideoDetails)
    VideoView ivMediaVideoDetails;
    @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @BindView(R.id.tvFavoriteCount) TextView tvFavoriteCount;
    @BindView(R.id.ibFavorite) ImageView ibFavorited;
    @BindView(R.id.ibRetweet) ImageView ibRetweeted;
    TwitterClient client;
    Tweet tweet;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra(TWEET_POSITION, 0);

        populateDetails();
    }

    public void populateDetails() {
//      Populate the views according to this data
        tvUserName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvScreenName.setText(getString(R.string.formatted_user_screen_name, tweet.getUser().getScreenName()));
        tvCreatedAt.setText(tweet.getRelativeCreatedAt());

        // Load the profile image
        Glide.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileImage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(tweet.getMedia().getMediaType(), "photo") || Objects.equals(tweet.getMedia().getMediaType(), "animated_gif")) {
                Glide.with(this)
                        .load(tweet.media.getMediaUrl())
                        .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                        .into(ivMediaImageDetails);
            } else {
                Glide.with(this)
                        .load(tweet.media.getMediaUrl())
                        .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                        .into(ivMediaImageDetails);
            }
        }
    }

    public void setFavorited() {
        // Set the tweet favorited status
        if(tweet.isFavorited()) {
            ibFavorited.setImageResource(R.drawable.ic_unfavorite);
        } else {
            ibFavorited.setImageResource(R.drawable.ic_favorite);
        }
        // Set the number of favorites
        if(tweet.getFavoriteCount() > 0) {
            tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
        } else {
            tvFavoriteCount.setText("");
        }
    }

    public void setRetweeted() {
        // Set the retweeted status
        if(tweet.isRetweeted()) {
            ibRetweeted.setImageResource(R.drawable.ic_unretweet);
        } else {
            ibRetweeted.setImageResource(R.drawable.ic_retweet);
        }
        // Set the number of retweets
        if(tweet.getRetweetCount() > 0) {
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        } else {
            tvRetweetCount.setText("");
        }
    }

     @OnClick(R.id.ibRetweet)
    public void putReply() {
        // Send the new tweet back to reply
        Intent i = new Intent(this, ReplyActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        startActivityForResult(i, REQUEST_CODE_DETAILS);
    }

    @OnClick(R.id.ibRetweet)
    public void putRetweet() {
        if(tweet.isRetweeted()) {
            unretweetTweet();
        } else {
            retweetTweet();
        }
    }

    @OnClick(R.id.ibFavoriteDetails)
    public void putFavorite() {
        if(tweet.isFavorited()) {
            unfavoriteTweet();
        } else {
            favoriteTweet();
        }
    }

    public void retweetTweet() {
        client.retweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.setRetweeted(true);
                    if(tweet.getRetweetCount() < newTweet.getRetweetCount()) {
                        tweet.setRetweetCount(newTweet.getRetweetCount());
                    }
                    setRetweeted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to retweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unretweetTweet() {
        client.unRetweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweet.setRetweeted(false);
                    if(tweet.getRetweetCount() > newTweet.getRetweetCount()) {
                        tweet.setRetweetCount(newTweet.getRetweetCount());
                    }
                    setRetweeted();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to unretweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void favoriteTweet() {
        client.favoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    tweet = Tweet.fromJSON(response);
                    if(!tweet.isFavorited()) {
                        tweet.setFavorited(true);
                    }
                    if(tweet.getFavoriteCount() < oldFavoriteCount + 1) {
                        tweet.setFavoriteCount(oldFavoriteCount + 1);
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void unfavoriteTweet() {
        client.unfavoriteTweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Endpoint returns original tweet; update the tweet
                    int oldFavoriteCount = tweet.getFavoriteCount();
                    tweet = Tweet.fromJSON(response);
                    if(tweet.isFavorited()) {
                        tweet.setFavorited(false);
                    }
                    if(tweet.getFavoriteCount() > oldFavoriteCount - 1) {
                        tweet.setFavoriteCount(oldFavoriteCount - 1);
                    }
                    setFavorited();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to favorite tweet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Return to the calling activity, send the new tweet and position
        Intent intent = new Intent();
        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        intent.putExtra(TWEET_POSITION, position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
