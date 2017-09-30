package com.marswilliams.apps.sweettweets.models;

import android.text.TextUtils;

import com.marswilliams.apps.sweettweets.helpers.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mars_williams on 9/28/17.
 */

public class Tweet {
    public String body;
    public long iud;
    public String createdAt;
    public User user;

    public Tweet() {
    }

    public Tweet(String body, long iud, String createdAt, User user) {
        this.body = body;
        this.iud = iud;
        this.createdAt = createdAt;
        this.user = new User();
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.iud = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }

    public String getRelativeCreatedAt() {
        if (TextUtils.isEmpty(createdAt)) {
            return "now";
        }
        return Utils.getRelativeTimeAgo(createdAt);
    }
}

