package com.marswilliams.apps.sweettweets.networking;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Changed
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Changed
//	public static final String REST_CONSUMER_KEY = "XaCRIfdZgPelA1HeQOSy7m66X";       // Changed
//	public static final String REST_CONSUMER_SECRET = "4Q3s2FrYRJKj03VgnQXqPHxY2kM59dolygLmjXqfhxucT1UOvU"; // Changed; todo obscure
	public static final String REST_CONSUMER_KEY = "eb8XyF71pTjpJjAK1j37OTEd3";       // Change this
	public static final String REST_CONSUMER_SECRET = "HbgDnxhHuTXYKp4AKqD01tHbKV0iZ5s0bfSnEB13z37VQ8mP3S"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context,
				REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(com.marswilliams.apps.sweettweets.R.string.intent_host),
						context.getString(com.marswilliams.apps.sweettweets.R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	public void getHomeTimeline(long max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// specify params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		if (max_id > 1) { // for consecutive calls to this endpoint
			params.put("max_id", max_id); // would we want this to update based on the last id we got?
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getAccountSettings(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/settings.json");
		getClient().get(apiUrl, handler);
	}

	public void getUserShow(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}

	public void postTweet(String status, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		getClient().post(apiUrl, params, handler);
	}
}
