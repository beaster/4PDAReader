package com.zetadex.pdareader.model;

import android.content.ContentValues;

import com.zetadex.pdareader.database.Columns;

public class NewsItem {

    private int mCategory;
    private String mData;
    private String mPhotoUrl;
    private String mTitle;
    private String mArticleUrl;


    public NewsItem(String title, String photoUrl, String articleUrl, int category) {
        mTitle = title;
        mPhotoUrl = photoUrl;
        mArticleUrl = articleUrl;
        mCategory = category;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        cv.put(Columns.NewsColumns.TITLE, mTitle);
        cv.put(Columns.NewsColumns.PHOTO_URL, mPhotoUrl);
        cv.put(Columns.NewsColumns.ARTICLE_URL, mArticleUrl);
        cv.put(Columns.NewsColumns.CATEGORY, mCategory);

        return cv;
    }

}
