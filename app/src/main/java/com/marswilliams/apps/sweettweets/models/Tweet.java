package com.marswilliams.apps.sweettweets.models;

import org.parceler.Parcel;

/**
 * Created by mars_williams on 9/28/17.
 */

@Parcel(analyze={Tweet.class})
public class Tweet {
    private String body;
    private long iud;
    private String createdAt;
    private User user;

    // empty constructor needed by the Parceler library
    public Tweet() {
    }

    public Tweet(String body, long iud, String createdAt, User user) {
        this.body = body;
        this.iud = iud;
        this.createdAt = createdAt;
        this.user = new User();
    }

}
