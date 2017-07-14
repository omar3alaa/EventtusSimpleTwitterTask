package com.example.mas.eventtussimpletwitter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Credentials;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.twitter.sdk.android.core.AuthToken;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;


public class Followers extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ListView followersList;
    long userID;
    ProgressDialog dialog;
    String myJSON;


    JSONArray Users = null;
     SimpleAdapter adapter=null;
    SharedPreferences.Editor editor;
    SwipeRefreshLayout SwipeContatiner;
    String AuthToken;
    String Cursor = "-1"; // Cursor initialization with -1 to show the first page of followers
    ArrayList<HashMap<String, String>> userList=new ArrayList<>();
    private static final String TAG_RESULTS = "users";
    @Override
    public void onBackPressed() {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        Intent intent = new Intent(Followers.this,Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers);
        followersList = (ListView) findViewById(R.id.followerList);
        SwipeContatiner = (SwipeRefreshLayout) findViewById(R.id.SwipeContatiner);
        // Here is why we saved userID in device settings to re-call it and use it in our httpConnection in getData() method
        sharedPreferences = getSharedPreferences(getString(R.string.myPrefs), MODE_PRIVATE);
        String UserID = getString(R.string.userID);
        userID = sharedPreferences.getLong(UserID, 0);
        AuthToken = sharedPreferences.getString(getString(R.string.AuthToken),"");
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();//To check if there is a network connection or not
        if(activeNetworkInfo!=null){ // if there is a internet connection, then we will getData from webservice
        getData();}
        else{
            // else if there is no internet connection, show the previously cached data
            try {
                userList = (ArrayList<HashMap<String, String>>) ObjectSerializer.deserialize(sharedPreferences.getString(getString(R.string.Followers),ObjectSerializer.serialize(new ArrayList<HashMap<String,String>>())));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            showList();
        }
        SwipeContatiner.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Here to refresh the activity and show the new data

               finish();
                startActivity(getIntent());
        }
        });
        // Configure the refreshing colors
        SwipeContatiner.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    protected void showList() {
        if(dialog!=null&&dialog.isShowing()) {
            dialog.dismiss();
        }
        if(SwipeContatiner.isRefreshing()){
            SwipeContatiner.setRefreshing(false);
        }
        //After finishing addList() we need to show these results in a beautiful list
        adapter = new SimpleAdapter(
                this, userList, R.layout.followersitem,
                new String[]{getString(R.string.Description), getString(R.string.Name), getString(R.string.Handle), getString(R.string.ProfilePic)},
                new int[]{R.id.bio, R.id.fullName, R.id.handle, R.id.profilePic}
        );

        adapter.setViewBinder(new CustomViewBinder()); //Custom view binder to populate every string with its ID let's read it together
        if (adapter.getCount() == 0) {
            followersList.setAdapter(null);

        } else {
            followersList.setAdapter(adapter);


        }

    }
    protected void addList() {

        try {

            JSONObject jsonObj = new JSONObject(myJSON);
            Users = jsonObj.getJSONArray(TAG_RESULTS);
            Cursor = jsonObj.getString("next_cursor_str");//get the next followers' page number
            for (int i = 0; i < Users.length(); i++) {
                JSONObject c = Users.getJSONObject(i);
                String bio = c.getString(getString(R.string.Description));
                String fullName = c.getString(getString(R.string.Name));
                String userName = c.getString(getString(R.string.Handle));
                String ProfilePic = c.getString(getString(R.string.ProfilePic));
                HashMap<String, String> user = new HashMap<>();
                user.put(getString(R.string.Description), bio);
                user.put(getString(R.string.Name), fullName);
                user.put(getString(R.string.Handle), userName);
                user.put(getString(R.string.ProfilePic), ProfilePic);
                userList.add(user);
            }
            //check if the next page (cursor) not equals 0 , if it is not equals 0 then we will go to the next page and add it to our userList by calling method getData() again
            // else if the next page (cursor) equals 0, then there is no another page of followers, so we will show our userList
            if (!Cursor.equals("0")){
                getData();
            }
            else if(Cursor.equals("0")){
                editor= sharedPreferences.edit();
                try {
                    editor.putString(getString(R.string.Followers),ObjectSerializer.serialize(userList));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                editor.apply();
                showList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //GetData method to make API Requests with Authorization header as Twitter API 1.1 states any request need oAuth header to be completed successfully
    public void getData() {
         class getFollowers extends AsyncTask<String, Void, String>{
             @Override
             protected void onPreExecute() {
                 super.onPreExecute();
                 if(Cursor.equals("-1")) {
                     dialog = new ProgressDialog(Followers.this);
                     dialog.setMessage("Loading, please wait");
                     dialog.setTitle("Getting followers!");
                     dialog.show();
                     dialog.setCancelable(false);
                 }
             }

             @Override
             protected String doInBackground(String... params) {
                 String data = "";
                 InputStream iStream ;
                 HttpURLConnection urlConnection ;

                 try {

                     URL url = new URL("https://api.twitter.com/1.1/followers/list.json?count=200&cursor="+Cursor+"&user_id="+userID);
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
                 System.out.print(result);
                 addList();


             }

             }
             getFollowers g = new getFollowers();
        g.execute();
         }
    }
