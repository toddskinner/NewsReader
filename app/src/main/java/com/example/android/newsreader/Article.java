package com.example.android.newsreader;

/**
 * Created by toddskinner on 4/24/17.
 */

public class Article {
    private String mSectionName;
    private String mWebPublicationDate;
    private String mWebTitle;
    private String mWebUrl;

    public Article (String sectionName, String webPublicationDate, String webTitle, String webUrl){
        mSectionName = sectionName;
        mWebPublicationDate = webPublicationDate;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
    }

    public String getSectionName(){
        return mSectionName;
    }

    public String getWebPublicationDate(){
        int splitIndex = mWebPublicationDate.indexOf("T");
        String extractDate = mWebPublicationDate.substring(0, splitIndex);
        String year = extractDate.substring(0,4);
        String month = extractDate.substring(5,7);
        String day = extractDate.substring(8,10);
        String formattedDate = month + "-" + day + "-" + year;
        return formattedDate;
    }

    public String getWebTitle(){
        return mWebTitle;
    }

    public String getWebUrl(){
        return mWebUrl;
    }
}
