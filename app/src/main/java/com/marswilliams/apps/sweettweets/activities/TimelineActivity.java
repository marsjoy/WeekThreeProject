package com.marswilliams.apps.sweettweets.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.marswilliams.apps.sweettweets.R.id.swipeContainer;

public class TimelineActivity extends AppCompatActivity implements
        ComposeTweetDialogFragment.OnTweetComposed {
    public static final String TWEET_POSITION = "tweetPosition";
    private static final int REQUEST_CODE_DETAILS = 42;
    private static final int REQUEST_CODE_REPLY = 32;

    @BindView(R.id.rvTweets)
    RecyclerView rvTweets;
    @BindView(swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabCompose)
    FloatingActionButton fabCompose;

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
                    fetchNextPage(max_id);
                }
            }
        });

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (broadcastReceiver.isInternetAvailable()) {
                    int position = TweetAdapter.getCount() - 1;
                    max_id = TweetAdapter.getAt(position).getTweetId();
                    fetchNextPage(max_id);
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

        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                addItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public void fetchNextPage(long max_id) {
        client.getHomeTimelinePage(max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(getString(R.string.twitter_client), response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(getString(R.string.twitter_client), response.toString());
                tweetAdapter.clear();
                addItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(getString(R.string.twitter_client), responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(getString(R.string.twitter_client), errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public void addItems(JSONArray response) {
        tweetAdapter.addAll(Tweet.fromJSONArray(response));
    }

    @OnClick(R.id.fabCompose)
    public void composeTweet(View v) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance();
        composeTweetDialogFragment.show(fm, "fragment_compose");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If returning successfully from details
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Deserialize the tweet and its position
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra(TWEET_POSITION, 0);
            tweets.set(position, newTweet);
            tweetAdapter.notifyItemChanged(position);
            rvTweets.scrollToPosition(position);
        // If returning successfully from reply
        } else if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_REPLY) {
            // Deserialize the tweet
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            // Get the current fragment, insert the tweet, and notify the adapter
            tweets.add(0, newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }
}
