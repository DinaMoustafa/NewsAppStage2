package com.example.dina.newsappstage2;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }


    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<>();
        // Try to parse the JSON string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // create JSON object from the JSON response string
            JSONObject root = new JSONObject(newsJSON);
            //extract JSONArray associated wuth key "results" which represents list of news
            JSONArray newsArray = root.getJSONObject("response").getJSONArray("results");
            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
// Get a single news at position i within the list news
                JSONObject newsObject = newsArray.getJSONObject(i);
                //Extract the value for the key called "sectionName"
                String sectionName = newsObject.getString("sectionName");
                //Extract the value for the key called "webTitle"
                String webTitle = newsObject.getString("webTitle");
                JSONArray tags = newsObject.getJSONArray("tags");
                String authorName = "";
                //if "tags" array is not null
                if (!(tags.length() == 0)) {
                    JSONObject currentObj = tags.getJSONObject(0);
                    StringBuilder authorFullName = new StringBuilder();
                    //Get Author first Name if it's not null
                    String authorFirstName = !currentObj.isNull("firstName") ? currentObj.getString("firstName") : "";
                    //Get Author last Name if it's not null
                    String authorLastName = !currentObj.isNull("lastName") ? currentObj.getString("lastName") : "";
                    authorFullName.append(authorFirstName).append(" ").append(authorLastName);
                    authorName = authorFullName.toString();
                }
                String newsPublicationDate = newsObject.getString("webPublicationDate");
                Date publicationDate = null;
                try {
                    publicationDate = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss").parse(newsPublicationDate);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "problem parsing news date", e);
                }
                String webUrl = newsObject.getString("webUrl");
// Create a new {@link News} object with the title, section name, publication date,
                // and url from the JSON response.
                News news = new News(sectionName, webTitle, publicationDate, authorName, webUrl);
// Add the new {@link News} to the list of news.
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "problem parsing News JSON response", e);
        }
// return list of news
        return newsList;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String urlString) {

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "problem building the URL", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        //  if url is null return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
//establish the HTTP Connection
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            Log.e(LOG_TAG, "RESPONSE CODE IS " + code);
            //if we have a successful request
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the json response results");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            //To handle that translation from raw data to human readable data.
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            // we wrap the inputStreamReader in a BufferedReader and we're ready to start reading lines.
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }


    public static List<News> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem making HTTP request", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> news = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link News}
        return news;
    }
}