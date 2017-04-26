package com.example.android.newsreader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by toddskinner on 4/24/17.
 */

public class ArticleListAdapter extends ArrayAdapter<Article> {

//    @BindView(R.id.article_title)
//    TextView articleTitleTextView ;
//
//    @BindView(R.id.section_name)
//    TextView sectionNameTextView;
//
//    @BindView(R.id.publication_date)
//    TextView publicationDateTextView;

    private Context mCon;

    public ArticleListAdapter(Activity context, ArrayList<Article> articles) {
        super(context, 0, articles);
        mCon = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.article, parent, false);
        }

        final Article currentArticle = getItem(position);

        TextView articleTitleTextView = (TextView) convertView.findViewById(R.id.article_title);
        String articleTitle = currentArticle.getWebTitle();
        articleTitleTextView.setText(articleTitle);

        TextView sectionNameTextView = (TextView) convertView.findViewById(R.id.section_name);
        String sectionName = currentArticle.getSectionName();
        sectionNameTextView.setText(sectionName);

        TextView publicationDateTextView = (TextView) convertView.findViewById(R.id.publication_date);
        String publicationDate = currentArticle.getWebPublicationDate();
        publicationDateTextView.setText(publicationDate);

        return convertView;
    }
}
