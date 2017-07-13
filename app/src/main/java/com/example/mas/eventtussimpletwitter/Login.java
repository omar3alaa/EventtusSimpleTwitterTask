package com.example.mas.eventtussimpletwitter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class Login extends AppCompatActivity {
    TwitterLoginButton loginButton;
    String TWITTER_KEY = "mPLzuwZetMxjVbfaZLTP0wpkW"; //The API KEY
    String TWITTER_SECRET = "whjnyHLdGGQCXqr33bQMlfrtn2LZStHuJN6Q6vCY8geWwTI9Vo"; //The API SECRET
    long userID;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // First we will  initialize Twitter with custom config
        final TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        // Here to check if there are another active sessions
       TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if(session!=null){
            // if there are another active sessions we will dismiss login screen and go to Followers Screen
            Intent intent = new Intent(Login.this,Followers.class);
            startActivity(intent);
        }
        else {
            // if there are no active sessions we will make user login with twitter account and initialize a new session
            Twitter.initialize(config);
            TwitterCore.getInstance();
            loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
            loginButton.setEnabled(true);
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    Log.d("Login", "Success");
                    // After a successful login we will save his userID in device settings to use it later, then we will display followers screen
                    sharedPreferences = getSharedPreferences(getString(R.string.myPrefs), MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    userID = result.data.getUserId();
                    editor.putLong(getString(R.string.userID), userID);
                    editor.apply();
                    Intent intent = new Intent(Login.this, Followers.class);
                    startActivity(intent);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("Login", exception.getMessage());
                }
            });
        }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}