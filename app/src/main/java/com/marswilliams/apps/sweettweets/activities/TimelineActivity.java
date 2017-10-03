package com.marswilliams.apps.sweettweets.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.adapters.TweetAdapter;
import com.marswilliams.apps.sweettweets.helpers.EndlessRecyclerViewScrollListener;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;
import com.marswilliams.apps.sweettweets.receivers.InternetCheckReceiver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.marswilliams.apps.sweettweets.R.id.swipeContainer;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE_COMPOSE = 20;

    static final String TAG = TimelineActivity.class.getSimpleName();

    TwitterClient client;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvTweets;
    InternetCheckReceiver broadcastReceiver;
    FloatingActionButton fab;
    private Toolbar toolbar;

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

        client = TwitterApplication.getRestClient();

        // find recyclerview
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        // init the arraylist (data source)
        tweets = new ArrayList<>();

        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);

        // find the swipe container
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swipeContainer);

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
                    max_id = TweetAdapter.getAt(position).getUid();
                    populateTimeline(max_id);
                }
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (broadcastReceiver.isInternetAvailable()) {
                    int position = TweetAdapter.getCount() - 1;
                    max_id = TweetAdapter.getAt(position).getUid();
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

        fab = (FloatingActionButton) findViewById(R.id.fabCompose);
        fab.setHapticFeedbackEnabled(true);
        fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE_COMPOSE);
            }
        });

        initialPopulateTimeline();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        } else {
            Toast.makeText(this, "Unable to submit tweet", Toast.LENGTH_SHORT).show();
        }
    }
}
