package com.example.mas.eventtussimpletwitter;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.internal.IdManager;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TimeLine extends ListActivity {

    String Handle;
    String background;
    String profilepic;
    String myJSON;
    ImageView ProfilePic,Background;
    String AuthToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
         ProfilePic = (ImageView)findViewById(R.id.profilePic);
         Background = (ImageView) findViewById(R.id.Background);
        SharedPreferences sharedpreference = getSharedPreferences(getString(R.string.myPrefs),MODE_PRIVATE);
        AuthToken = sharedpreference.getString(getString(R.string.AuthToken),"");
        Intent intent = getIntent();
        Handle = intent.getStringExtra("screen_name");
        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(Handle)
                .maxItemsPerRequest(10)
                .build();
        TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);
        getData();



    }
    public void getData() {
        class getInfo extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String data = "";
                InputStream iStream ;
                HttpURLConnection urlConnection ;
                try {
                    URL url = new URL("https://api.twitter.com/1.1/users/show.json?screen_name="+Handle);
                    // Creating an http connection to communicate with url
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    String jsonString = OAuthentication.appAuthentication(); // calling the appAuthentication method to get the access token and access type
                    JSONObject jsonObjectDocument = new JSONObject(jsonString);
                    String token = jsonObjectDocument.getString("token_type") + " "
                            + jsonObjectDocument.getString("access_token");
                    urlConnection.setRequestProperty("Authorization", token);
                    urlConnection.setRequestProperty("Content-Type",
                            "application/json");
                    urlConnection.connect();
                    // Reading data from url
                    iStream = urlConnection.getInputStream();

                    BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    //Just data parsing things
                    data = sb.toString();
                    Log.d("JOSN Response", data);
                    br.close();

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {

                //After reading url and parsing JSON result we will add this result in a List named userList by method addList();
                myJSON=result;
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    background=jsonObj.getString("profile_background_image_url");
                    profilepic = jsonObj.getString("profile_image_url");
                    Picasso.with(getApplicationContext()).load(background).into(Background);
                    Picasso.with(getApplicationContext()).load(profilepic).into(ProfilePic);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.print(result);


            }

        }
        getInfo g = new getInfo();
        g.execute();
    }


}
