package com.example.android.newsreader;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SavedArticlesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.saved_articles_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.saved_articles_recycler_view)
    RecyclerView mRecyclerView;

    public static final String LOG_TAG = SavedArticlesActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);
        ButterKnife.bind(this);

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return SavedArticlesLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        SavedArticlesAdapter adapter = new SavedArticlesAdapter(cursor);
        adapter.setHasStableIds(true);
        int columnCount = 1;
        GridLayoutManager sglm = new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(sglm);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class SavedArticlesAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Cursor mCursor;

        public SavedArticlesAdapter(Cursor cursor){
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(SavedArticlesLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            SavedArticlesContract.SavedArticlesEntry.buildArticleUri(getItemId(vh.getAdapterPosition()))));
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.articleTitleTextView.setText(mCursor.getString(SavedArticlesLoader.Query.TITLE));
            holder.publicationDateTextView.setText(mCursor.getString(SavedArticlesLoader.Query.PUBLISHED_DATE));
            String thumbnailUrl = mCursor.getString(SavedArticlesLoader.Query.THUMB_URL);
            Timber.d("thumbnailUrl");
            Timber.d(thumbnailUrl.toString());
            Picasso.with(holder.thumbnailView.getContext()).load(thumbnailUrl).into(holder.thumbnailView);
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView articleTitleTextView;
        public TextView publicationDateTextView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            articleTitleTextView = (TextView) view.findViewById(R.id.title);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
