package com.marswilliams.apps.sweettweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

/**
 * Created by mars_williams on 10/9/17.
 */

@Parcel
public class Media {

    private String mediaUrl;
    private String mediaType; //Type of uploaded media. Possible types include photo, video, and animated_gif

    public Media() { }

    public Media(JSONArray media) throws JSONException {
        try {
            mediaUrl = media.getJSONObject(0).getString("media_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mediaType = media.getJSONObject(0).getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}

