package com.example.android.newsreader;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by toddskinner on 4/29/17.
 */

public class SavedArticlesContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.newsreader";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SAVED = "savedarticles";

    public static final class SavedArticlesEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of saved articles.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVED;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single saved article.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY  + "/" + PATH_SAVED;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED).build();

        /** Matches: /items/ */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED).build();
        }

        /** Matches: /items/[_id]/ */
        public static Uri buildArticleUri(long _id) {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED).appendPath(Long.toString(_id)).build();
        }

        public static final String TABLE_NAME = "savedarticles";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ARTICLE_TITLE = "title";
        public static final String COLUMN_ARTICLE_DESCRIPTION = "description";
        public static final String COLUMN_ARTICLE_DATE = "date";
        public static final String COLUMN_ARTICLE_THUMBNAIL = "thumbnail";

        public static final String DEFAULT_SORT = COLUMN_ARTICLE_DATE + " DESC";
    }
}
