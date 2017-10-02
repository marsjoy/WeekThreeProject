package com.marswilliams.apps.sweettweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    // List the attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public User() {}

    public User(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.uid = object.getLong("id");
        this.screenName = object.getString("screen_name");
        this.profileImageUrl = object.getString("profile_image_url");
    }

    // Deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        // Extract and fill values
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

