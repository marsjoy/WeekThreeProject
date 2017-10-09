package com.marswilliams.apps.sweettweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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
    Tweet tweet;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

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

        Glide.with(this)
                .load(tweet.media.getMediaUrl())
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivMediaImageDetails);
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
