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
    public int favoriteCount;
    public boolean favorited;
    int retweetCount;
    boolean retweeted;

    public Tweet() {

    }

    public Tweet(JSONObject jsonObject) throws JSONException {
        this.body = jsonObject.getString("text");
        this.uid = jsonObject.getLong("id");
        this.createdAt = jsonObject.getString("created_at");
        this.user = User.fromJSON(jsonObject.getJSONObject("user"));
        try {
            this.favorited = jsonObject.getBoolean("favorited");
        } catch (JSONException e) {
            this.favorited = false;
        }
        try {
            this.favoriteCount = jsonObject.getInt("favorite_count");
        } catch (JSONException e) {
            this.favoriteCount = 0;
        }
        try {
            this.retweeted = jsonObject.getBoolean("retweeted");
        } catch (JSONException e) {
            this.retweeted = false;
        }
        try {
            this.retweetCount = jsonObject.getInt("retweet_count");
        } catch (JSONException e) {
            this.retweetCount = 0;
        }
    }

    // Deserialize the JSON; the exceptions will be thrown back up to the caller
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // Extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        try {
            tweet.favorited = jsonObject.getBoolean("favorited");
        } catch (JSONException e) {
            tweet.favorited = false;
        }
        try {
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
        } catch (JSONException e) {
            tweet.favoriteCount = 0;
        }
        try {
            tweet.retweeted = jsonObject.getBoolean("retweeted");
        } catch (JSONException e) {
            tweet.retweeted = false;
        }
        try {
            tweet.retweetCount = jsonObject.getInt("retweet_count");
        } catch (JSONException e) {
            tweet.retweetCount = 0;
        }
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
            }
        }

        return tweets;
    }

    public static String getRelativeCreatedAt(String createdAt) {
        if (TextUtils.isEmpty(createdAt)) {
            return "now";
        }
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public String getRelativeCreatedAt() {
        if (TextUtils.isEmpty(createdAt)) {
            return "now";
        }
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }
}
