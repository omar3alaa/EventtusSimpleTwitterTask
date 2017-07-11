package com.example.mas.eventtussimpletwitter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.twitter.sdk.android.core.Twitter;


public class Test extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView username = (TextView) findViewById(R.id.username);
        sharedPreferences=getSharedPreferences(getString(R.string.myPrefs),MODE_PRIVATE);
        String userName = getString(R.string.username);
        username.setText("Hello, "+sharedPreferences.getString(userName,""));

    }
}
