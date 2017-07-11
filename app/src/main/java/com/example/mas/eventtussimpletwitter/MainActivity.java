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


public class MainActivity extends AppCompatActivity {
    TwitterLoginButton loginButton;
    String TWITTER_KEY = "mPLzuwZetMxjVbfaZLTP0wpkW";
    String TWITTER_SECRET = "whjnyHLdGGQCXqr33bQMlfrtn2LZStHuJN6Q6vCY8geWwTI9Vo";
    String username;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        TwitterCore.getInstance();
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setEnabled(true);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Login","Success");
                sharedPreferences=getSharedPreferences(getString(R.string.myPrefs),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                username = result.data.getUserName();
                editor.putString(getString(R.string.username),username);
                editor.apply();
                Intent intent = new Intent(MainActivity.this,Test.class);
                startActivity(intent);

            }

            @Override
            public void failure(TwitterException exception) {
               Log.d("Login",exception.getMessage());
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
