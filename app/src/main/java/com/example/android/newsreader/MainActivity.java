package com.example.android.newsreader;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.example.android.newsreader.BuildConfig.API_KEY;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.logo)
    ImageView mLogoView;

    public static final String LOG_TAG = MainActivity.class.getName();
    private Adapter adapter;
    private String NYT_BASE_API_REQUEST_URL = "http://api.nytimes.com/svc/topstories/v2";
    private String BASE_API_REQUEST_URL = "https://newsapi.org/v1/articles";
    private static final int ARTICLE_LOADER_ID = 1;
    private List<Article> mListArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

//        listView.setEmptyView(emptyTextView);

        Timber.d("MainActivity");

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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String articleSource = sharedPrefs.getString(getString(R.string.settings_source_key), getString(R.string.settings_source_default));
        if(articleSource.equals(getResources().getString(R.string.settings_source_nyt_value))){
            mLogoView.setImageResource(R.drawable.nytimes);
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_bloomberg_value))){
            mLogoView.setImageResource(R.drawable.bloomberg);
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_businessinsider_value))){
            mLogoView.setImageResource(R.drawable.business_insider);
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_huffpo_value))){
            mLogoView.setImageResource(R.drawable.huffpost);
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_wapo_value))){
            mLogoView.setImageResource(R.drawable.washingtonpost);
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_wsj_value))){
            mLogoView.setImageResource(R.drawable.wallstreetjournal);
        } else {
            Intent savedArticlesIntent = new Intent(this, SavedArticlesActivity.class);
            startActivity(savedArticlesIntent);
        }

        ((MyApplication) getApplication()).startTracking();

        Tracker tracker = (((MyApplication) getApplication()).getTracker());
        tracker.setScreenName("Main Screen");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String articleSource = sharedPrefs.getString(
                getString(R.string.settings_source_key),
                getString(R.string.settings_source_default));
        String articleSortBy = "top";

        Tracker tracker = (((MyApplication) getApplication()).getTracker());

        if(articleSource.equals(getResources().getString(R.string.settings_source_nyt_value))){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read NYTimes")
                    .setLabel("NYTimes")
                    .build());
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_bloomberg_value))){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read Bloomberg")
                    .setLabel("Bloomberg")
                    .build());
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_businessinsider_value))){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read Business Insider")
                    .setLabel("Business Insider")
                    .build());
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_huffpo_value))){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read Huffington Post")
                    .setLabel("Huffington Post")
                    .build());
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_wapo_value))){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read Washington Post")
                    .setLabel("Washington Post")
                    .build());
        } else if(articleSource.equals(getResources().getString(R.string.settings_source_wsj_value))){
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read WSJ")
                    .setLabel("WSJ")
                    .build());
        } else {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Article sources")
                    .setAction("Read My Saved Articles")
                    .setLabel("My Saved Articles")
                    .build());
        }

        Uri baseUri = Uri.parse(BASE_API_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("source", articleSource);
        uriBuilder.appendQueryParameter("sortBy", articleSortBy);
        uriBuilder.appendQueryParameter("apiKey", API_KEY);

        Timber.d("uribuilder");
        Timber.d(uriBuilder.toString());

        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
//        adapter.clear();
        if (data != null && !data.isEmpty()) {
            adapter = new Adapter(data);
            adapter.setHasStableIds(true);
            Log.e("onLoadFinished", "Run onLoadFinished");
//            progressBar.setVisibility(View.GONE);
            mRecyclerView.setAdapter(adapter);
            int columnCount = 1;
            GridLayoutManager sglm =
                    new GridLayoutManager(this, columnCount);
            mRecyclerView.setLayoutManager(sglm);
        }
//        emptyTextView.setText(R.string.empty_list);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder>{

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
        public TextView publicationDateTextView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            articleTitleTextView = (TextView) view.findViewById(R.id.article_title);
            publicationDateTextView = (TextView) view.findViewById(R.id.publication_date);
        }
    }

    //reference for code below: http://stackoverflow.com/questions/27293960/swipe-to-dismiss-for-recyclerview

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Save this article?");

                builder.setNegativeButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveItem(position);

                        if (position == 0) {





                            //This crashes
//                            mListArticle.remove(position);
//                            adapter.notifyItemRemoved(position);

                            mListArticle.remove(position);
                            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
//                            adapter.notifyDataSetChanged();
                        } else {
                            adapter.notifyItemRemoved(position);
                        }
//                        mListArticle.remove(position);
                        return;
                    }
                }).setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.notifyItemRemoved(position + 1);
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                        return;
                    }
                }).show();
            }
        }
    };

    private void saveItem(int position){
        ContentValues contentValues = new ContentValues();
        Article currentArticle = mListArticle.get(position);
        contentValues.put(SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_TITLE, currentArticle.getWebTitle());
        contentValues.put(SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION, currentArticle.getDescription());
        contentValues.put(SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_DATE, currentArticle.getWebPublicationDate());
        contentValues.put(SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_THUMBNAIL, currentArticle.getThumbnailUrl());
        contentValues.put(SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_URL, currentArticle.getWebUrl());
        Uri newUri = getContentResolver().insert(SavedArticlesContract.SavedArticlesEntry.CONTENT_URI, contentValues);

        if (newUri != null) {
            Toast.makeText(this, R.string.toast_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
