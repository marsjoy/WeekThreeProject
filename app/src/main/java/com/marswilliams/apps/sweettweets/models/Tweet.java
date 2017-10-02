package com.marswilliams.apps.sweettweets.models;

import android.text.TextUtils;

import com.marswilliams.apps.sweettweets.helpers.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    // List out attributes
    public String body;
    public long uid; // Database ID for the tweet
    public User user;
    public String createdAt;

    public Tweet() {}
    public Tweet(JSONObject jsonObject) throws JSONException {
        this.body = jsonObject.getString("text");
        this.uid = jsonObject.getLong("id");
        this.createdAt = jsonObject.getString("created_at");
        this.user = User.fromJSON(jsonObject.getJSONObject("user"));
    }

    // Deserialize the JSON; the exceptions will be thrown back up to the caller
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // Extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray array) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int index = 0; index < array.length(); ++index) {
            try {
                Tweet tweet = Tweet.fromJSON(array.getJSONObject(index));
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    public String getRelativeCreatedAt() {
        if (TextUtils.isEmpty(createdAt)) {
            return "now";
        }
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public static String getRelativeCreatedAt(String createdAt) {
        if (TextUtils.isEmpty(createdAt)) {
            return "now";
        }
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
