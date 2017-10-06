package com.marswilliams.apps.sweettweets.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.adapters.TweetAdapter;
import com.marswilliams.apps.sweettweets.fragments.ComposeTweetDialogFragment;
import com.marswilliams.apps.sweettweets.helpers.EndlessRecyclerViewScrollListener;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;
import com.marswilliams.apps.sweettweets.receivers.InternetCheckReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.OnTweetComposed {
    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;

    TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    InternetCheckReceiver broadcastReceiver;

    private long max_id = 1;

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcastReceiver = new InternetCheckReceiver(this.rvTweets);
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        this.unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();

        // init the arraylist (data source)
        tweets = new ArrayList<>();

        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        // RecyclerView setup (layout manager, user adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                new LinearLayoutManager(this).getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) rvTweets.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (broadcastReceiver.isInternetAvailable()) {
                    int position = TweetAdapter.getCount() - 1;
                    max_id = TweetAdapter.getAt(position).getTweetId();
                    populateTimeline(max_id);
                }
            }
        });

        collapsingToolbar.setTitle("Sweet Tweets");

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (broadcastReceiver.isInternetAvailable()) {
                    int position = TweetAdapter.getCount() - 1;
                    max_id = TweetAdapter.getAt(position).getTweetId();
                    populateTimeline(max_id);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(swipeRefreshLayout.getRootView(), getString(R.string.snackbar_text_internet_lost),
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(
                R.color.twitter_blue,
                R.color.twitter_dark_gray,
                R.color.twitter_light_gray,
                R.color.twitter_red);
        fabCompose.setHapticFeedbackEnabled(true);
        initialPopulateTimeline();
    }

    @OnClick(R.id.fabCompose)
    public void composeTweet(View v) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance();
        composeTweetDialogFragment.show(fm, "fragment_compose");
    }

    private void populateTimeline(Long offset) {
        client.getHomeTimeline(offset, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                tweetAdapter.addAll(Tweet.fromJSONArray(response));
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) {
                    Log.d("DEBUG", errorResponse.toString());
                } else {
                    Log.d("DEBUG", "null error");
                }
            }
        });
    }

    private void initialPopulateTimeline() {
        tweetAdapter.clear();
        populateTimeline(max_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public void onTweetComposed(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }
}
