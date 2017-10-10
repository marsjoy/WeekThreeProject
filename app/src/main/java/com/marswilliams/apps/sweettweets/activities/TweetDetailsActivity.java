package com.marswilliams.apps.sweettweets.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import org.parceler.Parcels;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.marswilliams.apps.sweettweets.activities.TimelineActivity.TWEET_POSITION;


/**
 * Created by mars_williams on 10/6/17.
 */

public class TweetDetailsActivity extends AppCompatActivity {
    private static final int RESULT_CODE_DETAILS = 42;
    private static final int RESULT_CODE_REPLY = 32;

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
    @BindView(R.id.tvRetweetCount)
    TextView tvRetweetCount;
    @BindView(R.id.tvFavoriteCount)
    TextView tvFavoriteCount;
    @BindView(R.id.ibFavorited)
    ImageView ibFavorited;
    @BindView(R.id.ibRetweeted)
    ImageView ibRetweeted;
    @BindView(R.id.ibReplyDetails)
    ImageView ibReplyDetails;

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
        tvRetweetCount.setText(getString(R.string.count, tweet.getRetweetCount()));
        tvFavoriteCount.setText(getString(R.string.count, tweet.getFavoriteCount()));

        // Load the profile image
        Glide.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileImage);
        if (!(tweet.getMedia().getMediaUrl() == null)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(tweet.getMedia().getMediaType(), getString(R.string.photo)) || Objects.equals(tweet.getMedia().getMediaType(), getString(R.string.animated_gif))) {
                    Glide.with(this)
                            .load(tweet.getMedia().getMediaUrl())
                            .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                            .into(ivMediaImageDetails);
                    ivMediaVideoDetails.setVisibility(View.GONE);
                } else {
                    ivMediaVideoDetails.setVideoPath(tweet.getMedia().getMediaUrl());
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(ivMediaVideoDetails);
                    ivMediaVideoDetails.setMediaController(mediaController);
                    ivMediaVideoDetails.requestFocus();
                    ivMediaVideoDetails.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            ivMediaVideoDetails.start();
                        }
                    });
                    ivMediaImageDetails.setVisibility(View.GONE);
                }
            }
        } else {
            ivMediaImageDetails.setVisibility(View.GONE);
            ivMediaVideoDetails.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.ibReplyDetails)
    public void composeTweetReply(View v) {
        Intent intent = new Intent(getBaseContext(), TweetReplyActivity.class);
        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        intent.putExtra(TWEET_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Return to the calling activity, send the new tweet and position
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(TWEET_POSITION, position);
        setResult(RESULT_CODE_DETAILS, i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == RESULT_CODE_REPLY) {
            // Deserialize the tweet
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            // Get the current fragment, insert the tweet, and notify the adapter
            tweet = newTweet;
            ibReplyDetails.setImageResource(R.drawable.ic_comment);
        }
    }
}
