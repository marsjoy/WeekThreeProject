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
	public static final String REST_CONSUMER_KEY = "XaCRIfdZgPelA1HeQOSy7m66X";       // Changed
	public static final String REST_CONSUMER_SECRET = "4Q3s2FrYRJKj03VgnQXqPHxY2kM59dolygLmjXqfhxucT1UOvU"; // Changed; todo obscure

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

	//	User can view the tweets from their home timeline (4 points)
	//	User should be displayed the username, name, and body for each tweet
	//	User should be displayed the relative timestamp for each tweet "8m", "7h"
	//	User can view more tweets as they scroll with infinite pagination
	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		client.get(apiUrl, params, handler);
	}

	//	User can compose a new tweet (4 points)
	//	User can click a "Compose" icon in the AppBar on the top right
	//	User can then enter a new tweet and post this to twitter
	//	User is taken back to home timeline with new tweet visible in timeline
	//	Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
	public void composeTweet(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("/statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", "json");
		client.post(apiUrl, params, handler);
	}
}
