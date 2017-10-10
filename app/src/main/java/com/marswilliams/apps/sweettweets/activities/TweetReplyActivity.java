package com.marswilliams.apps.sweettweets.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.helpers.Utils;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.models.User;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.marswilliams.apps.sweettweets.activities.TimelineActivity.TWEET_POSITION;

/**
 * Created by mars_williams on 10/9/17.
 */

public class TweetReplyActivity extends AppCompatActivity {
    private static final int RESULT_CODE_REPLY = 32;
    final private int maxLength = 140;

    @BindView(R.id.tvReplyingTo)
    TextView tvReplyingTo;
    @BindView(R.id.tvCharacterCountReply)
    TextView tvCharacterCount;
    @BindView(R.id.etNewTweetReply)
    EditText etNewTweet;
    @BindView(R.id.ivComposeProfileImage)
    ImageView ivComposeProfileImage;

    Tweet tweet;
    TwitterClient client;
    int position;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_reply);

        context = getBaseContext();

        ButterKnife.bind(this);

        client = TwitterApplication.getRestClient();

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra(TWEET_POSITION, 0);

        // Show soft keyboard automatically and request focus to field
        etNewTweet.requestFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        etNewTweet.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(maxLength)
        });

        User.getCurrentUser(new User.UserCallbackInterface() {
            @Override
            public void onUserAvailable(User currentUser) {
                Glide.with(context)
                        .load(currentUser.getProfileImageUrl())
                        .placeholder(R.drawable.ic_profile_image_placeholder)
                        .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                        .into(ivComposeProfileImage);
            }
        });

        // Populate the @ replying to
        tvReplyingTo.setText(getString(R.string.replying_to, tweet.getUser().getScreenName()));

        Utils.initCharacterCount(context, etNewTweet, tvCharacterCount);
    }

    @OnClick(R.id.btnCloseCompose)
    public void closeCompose(View v) {
        // this should take you back to the timeline without posting the data
        Resources.Theme theme = getResources().newTheme();
        theme.applyStyle(R.style.AlertDialogCustom, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, theme))
                    .setIcon(R.drawable.pill_filled)
                    .setMessage(R.string.close_confirmation)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Return to the calling activity, send the new tweet and position
                            onReplyFinished(tweet);
                        }

                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
            Button nButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nButton.setTextColor(getResources().getColor(R.color.twitter_red, null));
            Button pButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pButton.setTextColor(getResources().getColor(R.color.twitter_red_light, null));
        }
    }

    @OnClick(R.id.btSubmitNewTweetReply)
    public void submitReply() {
        String newReplyText = getString(R.string.reply_text, tweet.getUser().getScreenName(), etNewTweet.getText().toString());
        // Send the request and parameters to the endpoint
        client.replyTweet(newReplyText, etNewTweet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    // Get the tweet
                    Tweet tweet = Tweet.fromJSON(response);
                    // Return to the calling activity, send the new tweet and position
                    onReplyFinished(tweet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_submit_reply, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_submit_reply, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, R.string.failed_to_submit_reply, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onReplyFinished(Tweet tweet) {
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(TWEET_POSITION, position);
        setResult(RESULT_CODE_REPLY, i);
        finish();
    }
}
