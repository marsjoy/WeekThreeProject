package com.marswilliams.apps.sweettweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.models.Tweet;

import org.parceler.Parcels;

import butterknife.ButterKnife;

/**
 * Created by mars_williams on 10/6/17.
 */

public class TweetDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TWEET = "TWEET";
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ButterKnife.bind(this);

        tweet = Parcels.unwrap(getIntent().getExtras().getParcelable(EXTRA_TWEET));
    }
}
