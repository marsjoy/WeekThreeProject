package com.marswilliams.apps.sweettweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.adapters.TweetAdapter;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    static final String TAG = TimelineActivity.class.getSimpleName();
    TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient();

        // find recyclerview
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        // init the arraylist (data source)
        tweets = new ArrayList<>();

        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        // RecyclerView setup (layout manager, user adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

        populateTimeline();
    }

    protected void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: " + response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                Log.d(TAG, "onSuccess: " + response.toString());

                // iterate through the JSON array
                // for each entry, deserialize the JSON object
                for (int i = 0; i < response.length(); i++) {

                    // convert each object to a Tweet model
                    Tweet tweet = null;
                    try {
                        // convert json to Tweet
                        tweet = Tweet.fromJson(response.getJSONObject(i));

                        // add that Tweet model to our data source
                        tweets.add(tweet);

                        // notify the adapter that we've added an item
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "onFailure: " + responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "onFailure: " + errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
