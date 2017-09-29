package com.marswilliams.apps.sweettweets.models;

import org.parceler.Parcel;

/**
 * Created by mars_williams on 9/28/17.
 */

@Parcel(analyze={User.class})
public class User {
    private String name;
    private long iud;
    private String screenName;
    private String profileImageUrl;

    // empty constructor needed by the Parceler library
    public User() {
    }

    public User(String name, long iud, String screenName, String profileImageUrl) {
        this.name = name;
        this.iud = iud;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
    }
}
