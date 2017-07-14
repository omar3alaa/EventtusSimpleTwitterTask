package com.example.mas.eventtussimpletwitter;

import android.util.Base64;
import android.util.Log;

import com.twitter.sdk.android.core.Twitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mas on 13/07/2017.
 */

class OAuthentication {
    private final static String TWITTER_KEY = "mPLzuwZetMxjVbfaZLTP0wpkW";
    private final static String TWITTER_SECRET = "whjnyHLdGGQCXqr33bQMlfrtn2LZStHuJN6Q6vCY8geWwTI9Vo";
    private static final String TAG = "TwitterUtils";
    static String appAuthentication() {

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
}
