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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    public static final String LOG_TAG = MainActivity.class.getName();
    private Adapter adapter;
    private String NYT_BASE_API_REQUEST_URL = "http://api.nytimes.com/svc/topstories/v2";
    private static final int ARTICLE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

        final View toolbarContainerView = findViewById(R.id.toolbar_container);

//        listView.setEmptyView(emptyTextView);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
//            progressBar.setVisibility(View.GONE);
//            emptyTextView.setText(R.string.no_connection_message);
            Toast.makeText(this, R.string.empty_list, Toast.LENGTH_SHORT).show();
        }



//        adapter = new ArticleListAdapter(this, new ArrayList<Article>());
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // Find the current book that was clicked on
//                Article currentArticle = adapter.getItem(position);
//
//                // Convert the String URL into a URI object (to pass into the Intent constructor)
//                Uri articleUri = Uri.parse(currentArticle.getWebUrl());
//
//                // Create a new intent to view the book URI
//                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
//
//                // Send the intent to launch a new activity
//                startActivity(websiteIntent);
//            }
//        });
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
        Timber.d("data");
        Timber.d(data.toString());
        adapter = new Adapter(data);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = 1;
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
//        adapter.clear();
//        progressBar.setVisibility(View.GONE);
//
//        if(data != null && !data.isEmpty()){
//            adapter.addAll(data);
//            Log.e("onLoadFinished", "Run onLoadFinished");
//        }
//        emptyTextView.setText(R.string.empty_list);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private List<Article> mListArticle;

        public Adapter(List<Article> listArticle) {
            mListArticle = listArticle;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Article currentArticle = mListArticle.get(viewHolder.getAdapterPosition());
                    Uri articleUri = Uri.parse(currentArticle.getWebUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                    startActivity(websiteIntent);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Article currentArticle = mListArticle.get(position);
            holder.articleTitleTextView.setText(currentArticle.getWebTitle());
            holder.sectionNameTextView.setText(currentArticle.getSectionName());
            holder.publicationDateTextView.setText(currentArticle.getWebPublicationDate());

            String thumbnailUrl = currentArticle.getThumbnailUrl();
            Timber.d("thumbnailUrl");
            Timber.d(thumbnailUrl.toString());
            Picasso.with(holder.thumbnailView.getContext()).load(thumbnailUrl).into(holder.thumbnailView);
        }

        @Override
        public int getItemCount() {
            return mListArticle.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView articleTitleTextView;
        public TextView sectionNameTextView;
        public TextView publicationDateTextView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            articleTitleTextView = (TextView) view.findViewById(R.id.article_title);
            sectionNameTextView = (TextView) view.findViewById(R.id.section_name);
            publicationDateTextView = (TextView) view.findViewById(R.id.publication_date);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
//            case R.id.action_delete_all_entries:
//                Intent settingsIntent = new Intent(this, SettingsActivity.class);
//                startActivity(settingsIntent);
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
