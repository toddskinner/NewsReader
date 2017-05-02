package com.example.android.newsreader;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Created by toddskinner on 4/29/17.
 */

public class SavedArticlesLoader extends CursorLoader {

    public static SavedArticlesLoader newAllArticlesInstance(Context context) {
        return new SavedArticlesLoader(context, SavedArticlesContract.SavedArticlesEntry.buildDirUri());
    }

//    public static SavedArticlesLoader newInstanceForItemId(Context context, long itemId) {
//        return new ArticleLoader(context, SavedArticlesContract.SavedArticlesEntry.buildDirUri());
//    }

    public SavedArticlesLoader(Context context, Uri uri) {
        super(context, uri, Query.ARTICLE, null, null, SavedArticlesContract.SavedArticlesEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] ARTICLE = {
                SavedArticlesContract.SavedArticlesEntry._ID,
                SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_TITLE,
                SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION,
                SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_DATE,
                SavedArticlesContract.SavedArticlesEntry.COLUMN_ARTICLE_THUMBNAIL,
        };

        int _ID = 0;
        int TITLE = 1;
        int DESRIPTION = 2;
        int PUBLISHED_DATE = 3;
        int THUMB_URL = 4;
    }
}
