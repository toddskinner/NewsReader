package com.example.android.newsreader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>{

    @BindView(R.id.empty_list)
    TextView emptyTextView;

    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;

    @BindView(R.id.list)
    ListView listView;

    public static final String LOG_TAG = MainActivity.class.getName();
    private ArticleListAdapter adapter;
    private String GUARDIAN_BASE_API_REQUEST_URL = "http://content.guardianapis.com/search?q=";
    private String NYT_BASE_API_REQUEST_URL = "http://api.nytimes.com/svc/topstories/v2";
    private static final int ARTICLE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

        listView.setEmptyView(emptyTextView);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyTextView.setText(R.string.no_connection_message);
        }

        adapter = new ArticleListAdapter(this, new ArrayList<Article>());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Article currentArticle = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentArticle.getWebUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String apiKey = "47ca4388-28bd-4e95-ad34-b6390a455847";
        String apiKey = "5c7ed8e8dac740df84a951ff67a60ac7";
        String format = ".json";
        String section = "national";
        String sectionAndFormat = section + format;

//        String section = sharedPrefs.getString(
//                getString(R.string.settings_topic_key),
//                getString(R.string.settings_topic_default));

        Uri baseUri = Uri.parse(NYT_BASE_API_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendPath(sectionAndFormat);
//        uriBuilder.appendEncodedPath(format);

//        uriBuilder.appendQueryParameter("q", section);
        uriBuilder.appendQueryParameter("api-key", apiKey);

        Timber.d(uriBuilder.toString());

        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        adapter.clear();
        progressBar.setVisibility(View.GONE);

        if(data != null && !data.isEmpty()){
            adapter.addAll(data);
            Log.e("onLoadFinished", "Run onLoadFinished");
        }
        emptyTextView.setText(R.string.empty_list);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
