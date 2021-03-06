package com.marswilliams.apps.sweettweets.models;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.marswilliams.apps.sweettweets.TwitterApplication;
import com.marswilliams.apps.sweettweets.networking.TwitterClient;
import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import cz.msebera.android.httpclient.Header;

@Parcel(analyze = User.class)
public class User extends SugarRecord<User> {

    // List the attributes
    public String name;
    public long twitterUserId;
    public String screenName;
    public String profileImageUrl;

    public User() {
    }

    public User(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.twitterUserId = object.getLong("id");
        this.screenName = object.getString("screen_name");
        this.profileImageUrl = object.getString("profile_image_url");
    }

    public User(String name, long twitterUserId, String screenName, String profileImageUrl) {
        this.name = name;
        this.twitterUserId = twitterUserId;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
    }


    // Deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        // Extract and fill values
        user.name = json.getString("name");
        user.twitterUserId = json.getLong("id");
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

    public long getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(long twitterUserId) {
        this.twitterUserId = twitterUserId;
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

    public interface UserCallbackInterface {
        void onUserAvailable(User currentUser);
    }

    public static void getCurrentUser(final UserCallbackInterface handler) {
        TwitterClient client = TwitterApplication.getRestClient();
        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    handler.onUserAvailable(User.fromJSON(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}

