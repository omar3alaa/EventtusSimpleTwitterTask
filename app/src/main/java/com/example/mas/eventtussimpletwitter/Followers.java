package com.example.mas.eventtussimpletwitter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
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
    String myJSON;
    final static String TWITTER_KEY = "mPLzuwZetMxjVbfaZLTP0wpkW";
    final static String TWITTER_SECRET = "whjnyHLdGGQCXqr33bQMlfrtn2LZStHuJN6Q6vCY8geWwTI9Vo";
    public static final String TAG = "TwitterUtils";

    JSONArray Users = null;
    String Cursor = "-1"; // Cursor initialization with -1 to show the first page of followers
    ArrayList<HashMap<String, String>> userList=new ArrayList<>();
    private static final String TAG_RESULTS = "users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers);
        followersList = (ListView) findViewById(R.id.followerList);
        // Here is why we saved userID in device settings to re-call it and use it in our httpConnection in getData() method
        sharedPreferences = getSharedPreferences(getString(R.string.myPrefs), MODE_PRIVATE);
        String UserID = getString(R.string.userID);
        userID = sharedPreferences.getLong(UserID, 0);
        getData();
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
                showList();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected void showList() {
        //After finishing addList() we need to show these results in a beautiful list
        SimpleAdapter adapter;
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




    public static String appAuthentication() {

        HttpURLConnection httpConnection = null;
        OutputStream outputStream;
        BufferedReader bufferedReader;
        StringBuilder response = null;

        try {
            URL url = new URL("https://api.twitter.com/oauth2/token");
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            String accessCredential = TWITTER_KEY + ":"
                    + TWITTER_SECRET;
            String authorization = "Basic "
                    + Base64.encodeToString(accessCredential.getBytes(),
                    Base64.NO_WRAP);
            String param = "grant_type=client_credentials";

            httpConnection.addRequestProperty("Authorization", authorization);
            httpConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpConnection.connect();

            outputStream = httpConnection.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            outputStream.close();
            // int statusCode = httpConnection.getResponseCode();
            // String reason =httpConnection.getResponseMessage();

            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream()));
            String line;
            response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            Log.d(TAG,
                    "POST response code: "
                            + String.valueOf(httpConnection.getResponseCode()));
            Log.d(TAG, "JSON response: " + response.toString());

        } catch (Exception e) {
            Log.e(TAG, "POST TAG = \"TwitterUtils\";\n" +
                    "\nerror: " + Log.getStackTraceString(e));

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }

        return response != null ? response.toString() : null;
    }
    //GetData method to make API Requests with Authorization header as Twitter API 1.1 states any request need oAuth header to be completed successfully
    public void getData() {
         class getFollowers extends AsyncTask<String, Void, String>{

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

                     String jsonString = appAuthentication();
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
                 //After reading url and parsing JSON result we will add this result in a List named userList by method addList
                 myJSON=result;
                 System.out.print(result);
                 addList();
             }

             }
             getFollowers g = new getFollowers();
        g.execute();
         }
    }
