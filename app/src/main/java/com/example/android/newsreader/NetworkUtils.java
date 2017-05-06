package com.example.android.newsreader;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toddskinner on 4/24/17.
 */

public final class NetworkUtils {

    private static final String LOG_TAG = MainActivity.class.getName();

    public static List<Article> extractArticlesFromJson(String articlesJSON){

        if (TextUtils.isEmpty(articlesJSON)){
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(articlesJSON);
            JSONArray articlesArray = baseJsonResponse.getJSONArray("articles");

            for(int i = 0; i < articlesArray.length(); i++){
                JSONObject jsonCurrentArticle = articlesArray.getJSONObject(i);
                String description = jsonCurrentArticle.getString("description");
                String publishedDate = jsonCurrentArticle.getString("publishedAt");
                String title = jsonCurrentArticle.getString("title");
                String url = jsonCurrentArticle.getString("url");
                String thumbnailUrl = jsonCurrentArticle.getString("urlToImage");
                articles.add(new Article(description, publishedDate, title, url, thumbnailUrl));
            }
        } catch (JSONException e){
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        return articles;
    }

    public static List<Article> fetchArticlesData(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Article> articles = extractArticlesFromJson(jsonResponse);


        return articles;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "Error with creating URL.", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news articles JSON results.", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // convert the input stream into a string containing the JSON response
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
