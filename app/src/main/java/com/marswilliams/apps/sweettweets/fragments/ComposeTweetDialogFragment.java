package com.marswilliams.apps.sweettweets.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.R;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.models.Tweet;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetDialogFragment extends DialogFragment {
    TwitterClient client;
    OnTweetComposed tweetComposed;
    Fragment fragment;

    public ComposeTweetDialogFragment() {
    }

    public static ComposeTweetDialogFragment newInstance() {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tweetComposed = (OnTweetComposed) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = TwitterApplication.getRestClient();

        // Get field from view
        final EditText etNewTweet = (EditText) view.findViewById(R.id.etNewTweet);

        // Show soft keyboard automatically and request focus to field
        etNewTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ;

        // Find submit button and set a click listener
        Button btSubmitNewTweet = (Button) view.findViewById(R.id.btSubmitNewTweet);
        btSubmitNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch new text
                String newTweetText = etNewTweet.getText().toString();
                client.postTweet(newTweetText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            // Get the tweet
                            Tweet tweet = Tweet.fromJSON(response);
                            tweetComposed.onTweetComposed(tweet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dismiss();
                        Toast.makeText(getContext(), "Submitted tweet", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (errorResponse != null) {
                            Log.d("DEBUG", errorResponse.toString());
                        } else {
                            Log.d("DEBUG", "null error");
                        }
                    }
                });
            }
        });

        // Find close button and set a click listener
        Button btCloseNewTweet = (Button) view.findViewById(R.id.btnClose);
        btCloseNewTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this should take you back to the timeline without posting the data
                Resources.Theme theme = getResources().newTheme();
                theme.applyStyle(R.style.AlertDialogCustom, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), theme))
                            .setIcon(R.drawable.pill_filled)
                            .setMessage("You haven't posted your tweet. Are you sure you want to close this activity?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }

                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        });
    }

    public interface OnTweetComposed {
        void onTweetComposed(Tweet tweet);
    }
}
