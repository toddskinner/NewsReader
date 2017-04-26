package com.example.android.newsreader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by toddskinner on 4/24/17.
 */

public class ArticleLoader extends AsyncTaskLoader<List<Article>>{

    private String mUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        if(mUrl == null){
            return null;
        }
        return NetworkUtils.fetchArticlesData(mUrl);
    }
}
