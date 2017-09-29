package com.marswilliams.apps.sweettweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mars_williams on 9/28/17.
 */

//@Parcel(analyze={Tweet.class})
public class Tweet {
//    private String body;
//    private long iud;
//    private String createdAt;
//    private User user;
//
//    // empty constructor needed by the Parceler library
//    public Tweet() {
//    }
//
//    public Tweet(String body, long iud, String createdAt, User user) {
//        this.body = body;
//        this.iud = iud;
//        this.createdAt = createdAt;
//        this.user = new User();
//    }

    public String body;
    public long iud;
    public String createdAt;
    public User user;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {

        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("body");
        tweet.iud = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return tweet;
    }
}
