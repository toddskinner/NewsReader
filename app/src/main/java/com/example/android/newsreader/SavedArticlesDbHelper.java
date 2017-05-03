package com.example.android.newsreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.newsreader.SavedArticlesContract.SavedArticlesEntry;

/**
 * Created by toddskinner on 4/29/17.
 */

public class SavedArticlesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "savedarticles.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + SavedArticlesEntry.TABLE_NAME + " ("
            + SavedArticlesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SavedArticlesEntry.COLUMN_ARTICLE_TITLE + " TEXT,"
            + SavedArticlesEntry.COLUMN_ARTICLE_DESCRIPTION + " TEXT,"
            + SavedArticlesEntry.COLUMN_ARTICLE_DATE + " TEXT,"
            + SavedArticlesEntry.COLUMN_ARTICLE_THUMBNAIL + " TEXT,"
            + SavedArticlesEntry.COLUMN_ARTICLE_URL + " TEXT" + ")";

    public SavedArticlesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SavedArticlesEntry.TABLE_NAME);
        onCreate(db);
    }
}
