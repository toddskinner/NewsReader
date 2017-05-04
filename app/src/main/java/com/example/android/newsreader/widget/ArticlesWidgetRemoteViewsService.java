package com.example.android.newsreader.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.newsreader.R;
import com.example.android.newsreader.SavedArticlesContract;

import timber.log.Timber;

/**
 * Created by toddskinner on 5/3/17.
 */

public class ArticlesWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] ARTICLES_COLUMNS = {
            SavedArticlesContract.SavedArticlesEntry._ID,
            SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_TITLE,
            SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION,
            SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_DATE,
            SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_THUMBNAIL,
            SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_URL
    };

    private static final int _ID = 0;
    private static final int TITLE = 1;
    private static final int DESCRIPTION = 2;
    private static final int PUBLISHED_DATE = 3;
    private static final int THUMB_URL = 4;
    private static final int ARTICLE_URL = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if(data != null){
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        SavedArticlesContract.SavedArticlesEntry.CONTENT_URI,
                        ARTICLES_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(data != null){
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_articles_item);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    Timber.d("We in ICE CREAM");
                    setRemoteContentDescription(views, data.getString(TITLE));
                }

                String title = data.getString(TITLE);
                String date = data.getString(PUBLISHED_DATE);

                views.setTextViewText(R.id.widget_article_title, title);
                views.setTextViewText(R.id.widget_article_date, date);

                final Intent fillInIntent = new Intent();
                Uri articleUri = Uri.parse(SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_URL);
                fillInIntent.setData(articleUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_article_title, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_articles_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position))
                    return data.getLong(_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
