package com.marswilliams.apps.sweettweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mars_williams on 9/28/17.
 */

//@Parcel(analyze={User.class})
public class User {
//    private String name;
//    private long iud;
//    private String screenName;
//    private String profileImageUrl;
//
//    // empty constructor needed by the Parceler library
//    public User() {
//    }
//
//    public User(String name, long iud, String screenName, String profileImageUrl) {
//        this.name = name;
//        this.iud = iud;
//        this.screenName = screenName;
//        this.profileImageUrl = profileImageUrl;
//    }

    public String name;
    public long iud;
    public String screenName;
    public String profileImageUrl;

    public static User fromJson(JSONObject jsonObject) throws JSONException {

        User user = new User();
        user.name = jsonObject.getString("name");
        user.iud = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");

        return user;
    }
}
